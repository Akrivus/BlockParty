package block_party.conversation.generation;

import block_party.conversation.model.ScenePackProject;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.TriggerTypes;
import block_party.conversation.model.PackCondition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;

final class GenerationSchemas {
    private GenerationSchemas() {
    }

    static JsonObject forType(Class<?> type) {
        if (type == ArcPlan.class) return arcPlan();
        if (type == GenerationReview.class) return review();
        if (type == DialogueRevision.class) return revision();
        if (type == DialoguePass.class) return schemaFor(type, false);
        if (type == ScenePackProject.class) return schemaFor(type, false);
        return null;
    }

    private static JsonObject arcPlan() {
        JsonObject beat = object(
                property("id", string()),
                property("purpose", string()),
                property("session", integer()),
                property("playerAgency", string()),
                property("gameplayRequirement", string()));
        return object(
                property("premise", string()),
                property("characterArc", string()),
                property("beats", array(beat)),
                property("outcomes", array(string())));
    }

    private static JsonObject review() {
        JsonObject finding = object(
                property("severity", string()),
                property("node", nullableString()),
                property("code", string()),
                property("message", string()));
        return object(property("findings", array(finding)));
    }

    private static JsonObject revision() {
        JsonObject alternative = object(
                property("text", string()),
                property("responseLabels", array(string())),
                property("rationale", string()));
        return object(property("alternatives", array(alternative)));
    }

    private static JsonObject object(Property... properties) {
        JsonObject schema = typed("object");
        JsonObject fields = new JsonObject();
        JsonArray required = new JsonArray();
        for (Property property : properties) {
            fields.add(property.name(), property.schema());
            required.add(property.name());
        }
        schema.add("properties", fields);
        schema.add("required", required);
        schema.addProperty("additionalProperties", false);
        return schema;
    }

    private static JsonObject array(JsonObject items) {
        JsonObject schema = typed("array");
        schema.add("items", items);
        return schema;
    }

    private static JsonObject nullableString() {
        JsonObject schema = new JsonObject();
        JsonArray types = new JsonArray();
        types.add("string");
        types.add("null");
        schema.add("type", types);
        return schema;
    }

    private static JsonObject string() {
        return typed("string");
    }

    private static JsonObject integer() {
        return typed("integer");
    }

    private static JsonObject typed(String type) {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", type);
        return schema;
    }

    private static JsonObject schemaFor(Type type, boolean nullable) {
        if (type == String.class) return nullable ? nullableString() : string();
        if (type == int.class || type == Integer.class) return integer();
        if (type == boolean.class || type == Boolean.class) return typed("boolean");
        if (type == JsonElement.class) return typed("null");
        if (type == JsonObject.class) return speaker();
        if (type instanceof ParameterizedType parameterized
                && parameterized.getRawType() == java.util.List.class) {
            return array(schemaFor(parameterized.getActualTypeArguments()[0], false));
        }
        if (type instanceof Class<?> value && value.isEnum()) {
            JsonObject schema = nullable ? nullableString() : string();
            JsonArray values = new JsonArray();
            for (Object constant : value.getEnumConstants()) values.add(constant.toString());
            if (nullable) values.add(com.google.gson.JsonNull.INSTANCE);
            schema.add("enum", values);
            return schema;
        }
        if (type instanceof Class<?> value && value.isRecord()) {
            RecordComponent[] components = value.getRecordComponents();
            Property[] properties = new Property[components.length];
            for (int index = 0; index < components.length; index++) {
                RecordComponent component = components[index];
                properties[index] = property(
                        component.getName(),
                        value == SceneNode.class && component.getName().equals("trigger")
                                ? trigger()
                                : schemaFor(component.getGenericType(), !component.getType().isPrimitive()));
            }
            JsonObject object = object(properties);
            if (value == PackCondition.class) describeConditionFields(object);
            if (value == DialoguePatch.class) describeDialogueFields(object);
            return nullable ? nullable(object) : object;
        }
        throw new IllegalArgumentException("No generation schema mapping for " + type);
    }

    private static JsonObject speaker() {
        return object(
                property("emotion", nullableString()),
                property("animation", nullableString()));
    }

    private static JsonObject trigger() {
        JsonObject schema = nullableString();
        JsonArray values = new JsonArray();
        TriggerTypes.values().forEach(values::add);
        values.add(com.google.gson.JsonNull.INSTANCE);
        schema.add("enum", values);
        schema.addProperty("description", "Block Party scene event. Use right_click for ordinary Moe interaction.");
        return schema;
    }

    private static void describeConditionFields(JsonObject schema) {
        JsonObject properties = schema.getAsJsonObject("properties");
        properties.getAsJsonObject("item").addProperty(
                "description",
                "Resource ID for HAS_ITEM, HELD_ITEM, MOE_HAS_ITEM, and BLOCK conditions. "
                        + "BLOCK IDs belong here, not in marker.");
        properties.getAsJsonObject("marker").addProperty(
                "description",
                "Named timestamp used by ELAPSED_TIME. Never put a Minecraft block or item ID here.");
    }

    private static void describeDialogueFields(JsonObject schema) {
        schema.getAsJsonObject("properties").getAsJsonObject("text").addProperty(
                "description",
                "Words spoken aloud by the Moe, rendered verbatim. Never narration, brackets, stage direction, or a speaker label.");
    }

    private static JsonObject nullable(JsonObject value) {
        JsonObject schema = new JsonObject();
        JsonArray alternatives = new JsonArray();
        alternatives.add(value);
        alternatives.add(typed("null"));
        schema.add("anyOf", alternatives);
        return schema;
    }

    private static Property property(String name, JsonObject schema) {
        return new Property(name, schema);
    }

    private record Property(String name, JsonObject schema) {
    }
}
