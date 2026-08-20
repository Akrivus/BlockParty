package block_party.conversation.compile;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ChangeOperation;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.PackAction;
import block_party.conversation.model.PackCondition;
import block_party.conversation.model.ProjectIndex;
import block_party.conversation.model.StateScope;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

final class MechanicsCompiler {
    private MechanicsCompiler() {
    }

    static JsonElement condition(PackCondition condition, ProjectIndex index) {
        if (condition.type() == ConditionType.RAW) {
            return condition.raw().deepCopy();
        }
        if (condition.type() == ConditionType.SCENE_FILTER) {
            JsonObject result = condition.filter().deepCopy();
            String type = result.get("type").getAsString();
            if (!type.contains(":")) result.addProperty("type", "block_party:" + type);
            JsonObject payload = result.deepCopy();
            payload.remove("type");
            result.entrySet().removeIf(entry -> !"type".equals(entry.getKey()));
            if (!payload.isEmpty()) result.add("filter", payload);
            return result;
        }
        if (condition.type() == ConditionType.ALWAYS) {
            return new JsonPrimitive("block_party:always");
        }
        JsonObject payload = new JsonObject();
        String type;
        switch (condition.type()) {
            case HAS_COOKIE -> {
                type = scoped(condition.scope(), "has_cookie");
                payload.addProperty("name", index.compiledState(condition.state()));
            }
            case COUNTER -> {
                type = scoped(condition.scope(), "counter");
                payload.addProperty("name", index.compiledState(condition.state()));
                payload.addProperty("operation", lower(condition.comparison() == null ? "equals" : condition.comparison().name()));
                payload.addProperty("value", condition.value());
            }
            case HAS_ITEM -> {
                type = condition.scope() == StateScope.NPC ? "moe_has_item" : "player_has_item";
                payload.addProperty("item", condition.item());
                payload.addProperty("count", Math.max(1, condition.count()));
            }
            case HELD_ITEM -> {
                type = condition.scope() == StateScope.NPC ? "held_item" : "player_held_item";
                payload.addProperty("item", condition.item());
            }
            case MOE_HAS_ITEM -> {
                type = "moe_has_item";
                payload.addProperty("item", condition.item());
                payload.addProperty("count", Math.max(1, condition.count()));
            }
            case BLOCK -> {
                type = "block";
                payload.addProperty("name", condition.item());
            }
            case ELAPSED_TIME -> {
                type = "elapsed_since_marker";
                payload.addProperty("scope", lower(scope(condition.scope()).name()));
                payload.addProperty("name", condition.marker());
                payload.addProperty("min_game_days", Math.max(0, condition.minGameDays()));
            }
            default -> throw new IllegalArgumentException("Unsupported condition " + condition.type());
        }
        if (condition.not()) {
            payload.addProperty("not", true);
        }
        JsonObject result = new JsonObject();
        result.addProperty("type", "block_party:" + type);
        result.add("filter", payload);
        return result;
    }

    static JsonElement action(PackAction action, ProjectIndex index) {
        if (action.type() == ActionType.RAW) {
            return action.raw().deepCopy();
        }
        if (action.type() == ActionType.END) {
            return new JsonPrimitive("block_party:end");
        }
        JsonObject result = new JsonObject();
        JsonObject payload = new JsonObject();
        switch (action.type()) {
            case SET_COOKIE, DELETE_COOKIE -> {
                result.addProperty("type", "block_party:" + scoped(action.scope(), "cookie"));
                payload.addProperty("operation", action.type() == ActionType.DELETE_COOKIE ? "delete" : "set");
                payload.addProperty("name", index.compiledState(action.state()));
                payload.addProperty("value", action.value() == null ? "true" : action.value());
            }
            case CHANGE_COUNTER -> {
                result.addProperty("type", "block_party:" + scoped(action.scope(), "counter"));
                payload.addProperty("operation", lower((action.operation() == null ? ChangeOperation.ADD : action.operation()).name()));
                payload.addProperty("name", index.compiledState(action.state()));
                payload.addProperty("value", action.amount());
            }
            case GIVE_ITEM -> {
                result.addProperty("type", "block_party:give_item");
                payload.addProperty("item", action.item());
                payload.addProperty("count", Math.max(1, action.count()));
                payload.addProperty("target", value(action.target(), "player"));
            }
            case TAKE_ITEM -> {
                result.addProperty("type", "block_party:take_item");
                payload.addProperty("item", action.item());
                payload.addProperty("count", Math.max(1, action.count()));
                payload.addProperty("source", value(action.source(), "player"));
                payload.addProperty("destination", value(action.destination(), "moe"));
            }
            case ACCEPT_OFFERED_GIFT -> result.addProperty("type", "block_party:accept_offered_gift");
            case MARK_TIME -> {
                result.addProperty("type", "block_party:mark_time");
                payload.addProperty("scope", lower(scope(action.scope()).name()));
                payload.addProperty("name", action.marker());
            }
            case REMEMBER_LOCATION, FORGET_LOCATION -> {
                result.addProperty("type", "block_party:" + (action.type() == ActionType.REMEMBER_LOCATION
                        ? "remember_location" : "forget_location"));
                payload.addProperty("scope", lower(scope(action.scope()).name()));
                payload.addProperty("name", action.location());
                if (action.type() == ActionType.REMEMBER_LOCATION) {
                    payload.addProperty("source", value(action.source(), "moe"));
                }
            }
            case ASSIGN_LOCATION -> {
                result.addProperty("type", "block_party:assign_location");
                payload.addProperty("scope", lower(scope(action.scope()).name()));
                payload.addProperty("name", action.location());
                if (action.id() != null && !action.id().isBlank()) payload.addProperty("id", action.id());
                directiveFields(action, payload);
            }
            case ASSIGN_TARGET -> {
                result.addProperty("type", "block_party:assign_target");
                payload.addProperty("selector", value(action.target(), "owner"));
                if (action.id() != null && !action.id().isBlank()) payload.addProperty("id", action.id());
                directiveFields(action, payload);
            }
            case CLEAR_ASSIGNMENT -> result.addProperty("type", "block_party:clear_assignment");
            case ASSIGN_NEAR_BLOCK -> {
                result.addProperty("type", "block_party:assign_near_block");
                payload.addProperty("block", action.block());
                if (action.id() != null && !action.id().isBlank()) payload.addProperty("id", action.id());
                payload.addProperty("search_radius", action.searchRadius() <= 0 ? 16 : action.searchRadius());
                payload.addProperty("vertical_radius", Math.max(0, action.verticalRadius()));
                directiveFields(action, payload);
            }
            case CONSUME_ASSIGNMENT_RESULT -> result.addProperty("type", "block_party:consume_assignment_result");
            case WAIT_TICKS -> {
                result.addProperty("type", "block_party:wait_ticks");
                payload.addProperty("ticks", Math.max(0, action.ticks()));
            }
            case WAIT_RANDOM_TICKS -> {
                result.addProperty("type", "block_party:wait_random_ticks");
                payload.addProperty("min_ticks", Math.max(0, action.minTicks()));
                payload.addProperty("max_ticks", Math.max(action.minTicks(), action.maxTicks()));
            }
            case PLAY_ANIMATION -> {
                result.addProperty("type", "block_party:play_animation");
                payload.addProperty("animation", value(action.animation(), "DEFAULT"));
                payload.addProperty("ticks", Math.max(0, action.ticks()));
            }
            case SET_EMOTION -> {
                result.addProperty("type", "block_party:set_emotion");
                payload.addProperty("emotion", value(action.emotion(), "NORMAL"));
                payload.addProperty("ticks", Math.max(0, action.ticks()));
            }
            case SIT, STAND, JUMP, SWING_HAND, LOOK_AT_ASSIGNMENT -> result.addProperty("type", "block_party:" + switch (action.type()) {
                case SIT -> "sit";
                case STAND -> "stand";
                case JUMP -> "jump";
                case SWING_HAND -> "swing_hand";
                default -> "look_at_assignment";
            });
            case OPEN_INVENTORY -> result.addProperty("type", "block_party:open_inventory");
            case START_FOLLOW -> {
                result.addProperty("type", "block_party:start_follow_session");
                payload.addProperty("intent", value(action.intent(), "follow_request"));
                payload.addProperty("ticks", Math.max(0, action.ticks()));
                payload.addProperty("can_change_dimension", action.canChangeDimension());
                payload.addProperty("trigger_scene", action.triggerScene());
            }
            case CLEAR_FOLLOW -> result.addProperty("type", "block_party:clear_follow_session");
            default -> throw new IllegalArgumentException("Unsupported action " + action.type());
        }
        if (!payload.isEmpty()) {
            result.add("action", payload);
        }
        return result;
    }

    private static void directiveFields(PackAction action, JsonObject payload) {
        payload.addProperty("speed", action.speed() <= 0.0D ? 1.0D : action.speed());
        payload.addProperty("arrival_radius", action.arrivalRadius() <= 0.0D ? 2.0D : action.arrivalRadius());
        payload.addProperty("timeout_ticks", action.timeoutTicks() <= 0 ? 1200 : action.timeoutTicks());
    }

    private static String scoped(StateScope scope, String base) {
        return switch (scope(scope)) {
            case NPC -> base;
            case PLAYER -> "player_" + base;
            case WORLD -> "world_" + base;
        };
    }

    private static StateScope scope(StateScope scope) {
        return scope == null ? StateScope.NPC : scope;
    }

    private static String lower(String value) {
        return value.toLowerCase(java.util.Locale.ROOT);
    }

    private static String value(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.toLowerCase(java.util.Locale.ROOT);
    }
}
