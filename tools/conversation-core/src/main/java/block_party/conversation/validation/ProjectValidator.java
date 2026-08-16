package block_party.conversation.validation;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.NodeType;
import block_party.conversation.model.PackAction;
import block_party.conversation.model.PackCondition;
import block_party.conversation.model.ProjectIndex;
import block_party.conversation.model.ResponseEdge;
import block_party.conversation.model.ResponseCues;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.SpeakerPresentation;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.model.SceneFilterCatalog;
import block_party.conversation.model.StateDeclaration;
import block_party.conversation.model.StateReference;
import block_party.conversation.model.StateType;
import block_party.conversation.model.TransitionType;
import block_party.conversation.model.TriggerTypes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class ProjectValidator {
    private static final Pattern ID = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern RESOURCE = Pattern.compile("[#]?[a-z0-9_.-]+:[a-z0-9_./-]+");
    public ValidationReport validate(ScenePackProject project) {
        List<Diagnostic> issues = new ArrayList<>();
        if (project == null || project.pack() == null) {
            issues.add(error("MISSING_PACK", null, "Project metadata is required."));
            return new ValidationReport(issues);
        }
        if (project.projectFormat() != 2) {
            issues.add(error("UNSUPPORTED_PROJECT_FORMAT", null, "Iteration two requires projectFormat 2."));
        }
        if (project.target() == null || !"block_party".equals(project.target().mod())) {
            issues.add(error("INVALID_TARGET", null, "Target mod must be block_party."));
        }
        if (!validId(project.pack().id()) || !validId(project.pack().namespace())) {
            issues.add(error("INVALID_PACK_ID", null, "Pack id and namespace must use lowercase resource characters."));
        }

        ProjectIndex index = new ProjectIndex(project);
        validateState(project, index, issues);
        validateContract(project, index, issues);
        Map<String, SceneNode> nodes = index.nodes();
        if (nodes.size() != project.nodes().size()) {
            issues.add(error("DUPLICATE_NODE", null, "Node ids must be unique."));
        }
        for (SceneNode node : project.nodes()) {
            validateNode(node, project, index, issues);
        }
        if (!nodes.containsKey(project.entry())) {
            issues.add(error("MISSING_ENTRY", project.entry(), "Entry does not reference an existing node."));
            return new ValidationReport(issues);
        }
        for (SceneNode node : nodes.values()) {
            targets(node).stream().filter(target -> !nodes.containsKey(target))
                    .forEach(target -> issues.add(error("BROKEN_EDGE", node.id(), "Target '" + target + "' does not exist.")));
        }

        Set<String> reachable = reachable(project.entry(), nodes);
        // Gameplay gates are independently triggered roots after an explicit edge establishes their state.
        project.nodes().stream().filter(node -> node.type() == NodeType.GAMEPLAY_GATE).forEach(node -> reachable.addAll(reachable(node.id(), nodes)));
        nodes.keySet().stream().filter(id -> !reachable.contains(id))
                .forEach(id -> issues.add(warning("UNREACHABLE_NODE", id, "Node is not reachable from an entry or gameplay gate.")));
        if (reachable.stream().map(nodes::get).noneMatch(ProjectValidator::hasEnding)) {
            issues.add(error("NO_REACHABLE_END", project.entry(), "The graph has no reachable ending."));
        }
        analyzeStateUsage(project, issues);
        analyzeRoots(project, issues);
        analyzeRewards(project, nodes, issues);
        return new ValidationReport(issues);
    }

    private static void validateContract(ScenePackProject project, ProjectIndex index, List<Diagnostic> issues) {
        for (StateReference provided : project.contract().provides()) {
            StateDeclaration local = index.state().get(provided.id());
            if (local == null) {
                issues.add(error("UNKNOWN_PROVIDED_STATE", null, "Provided state '" + provided.id() + "' is not declared locally."));
            } else if (local.type() != provided.type() || local.scope() != provided.scope()) {
                issues.add(error("PROVIDED_STATE_MISMATCH", null, "Provided state '" + provided.id() + "' does not match its declaration."));
            }
        }
        Set<String> declaredOutcomes = new HashSet<>(project.contract().outcomes());
        for (SceneNode node : project.nodes()) {
            if (node.type() == NodeType.END && node.ending() != null && !declaredOutcomes.contains(node.ending())) {
                issues.add(error("UNDECLARED_OUTCOME", node.id(), "Ending '" + node.ending() + "' is absent from the pack contract."));
            }
        }
    }

    private static void validateState(ScenePackProject project, ProjectIndex index, List<Diagnostic> issues) {
        if (index.state().size() != project.state().size()) {
            issues.add(error("DUPLICATE_STATE", null, "State ids must be unique."));
        }
        for (StateDeclaration state : project.state()) {
            if (!validId(state.id()) || state.type() == null || state.scope() == null) {
                issues.add(error("INVALID_STATE", null, "Every local state needs a valid id, type, and scope."));
            }
            if (state.type() == StateType.COUNTER && state.minimum() != null && state.maximum() != null
                    && state.minimum() > state.maximum()) {
                issues.add(error("INVALID_COUNTER_BOUNDS", null, "Counter '" + state.id() + "' has inverted bounds."));
            }
        }
    }

    private static void validateNode(SceneNode node, ScenePackProject project, ProjectIndex index, List<Diagnostic> issues) {
        if (node == null || !validId(node.id()) || node.type() == null) {
            issues.add(error("INVALID_NODE", node == null ? null : node.id(), "Node id and type are required."));
            return;
        }
        for (PackCondition condition : node.conditions()) {
            validateCondition(condition, node.id(), project, index, issues);
        }
        validateActions(node.actions(), node.id(), project, index, issues);
        if (!TriggerTypes.valid(node.trigger())) {
            issues.add(error("INVALID_TRIGGER", node.id(),
                    "Unknown trigger '" + node.trigger() + "'. Use right_click for ordinary Moe interaction."));
        }
        validateSpeaker(node, issues);
        if (node.selection() != null && (node.selection().weight() < 1 || node.selection().cooldownTicks() < 0)) {
            issues.add(error("INVALID_SELECTION", node.id(), "Scene selection weight must be positive and cooldown cannot be negative."));
        }
        if (node.type() == NodeType.GAMEPLAY_GATE && !node.id().equals(project.entry())
                && node.conditions().stream().noneMatch(condition -> condition.type() == ConditionType.HAS_COOKIE
                        || condition.type() == ConditionType.COUNTER)) {
            issues.add(error("UNGATED_GAMEPLAY_GATE", node.id(),
                    "Later gameplay gates require a HAS_COOKIE or COUNTER condition written by an earlier interaction."));
        }
        for (ResponseEdge edge : node.responses()) {
            if ((edge.target() == null || edge.target().isBlank())
                    && edge.transition() != TransitionType.PACK_EXIT) {
                issues.add(error("MISSING_TARGET", node.id(),
                        "Response target is required unless transition is PACK_EXIT."));
            }
        }
        if (node.type() == NodeType.DIALOGUE) {
            if (node.text() == null || node.text().isBlank()) {
                issues.add(error("EMPTY_DIALOGUE", node.id(), "Dialogue text must not be empty."));
            }
            if (node.responses().isEmpty()) {
                issues.add(error("NO_RESPONSES", node.id(), "Dialogue must have at least one response."));
            }
            if (node.responses().size() > SceneNode.MAX_RESPONSES) {
                issues.add(error("TOO_MANY_RESPONSES", node.id(), "Dialogue has " + node.responses().size()
                        + " responses; the dialogue UI supports at most " + SceneNode.MAX_RESPONSES + "."));
            }
            Set<String> cues = new HashSet<>();
            for (ResponseEdge edge : node.responses()) {
                String cue = unqualify(edge.cue());
                if (!ResponseCues.valid(cue)) {
                    issues.add(error("INVALID_CUE", node.id(), "Unknown response cue '" + edge.cue() + "'."));
                } else if (!cues.add(cue)) {
                    issues.add(error("DUPLICATE_CUE", node.id(), "Response cue '" + cue + "' is repeated."));
                }
                if (edge.transition() == TransitionType.IMMEDIATE) {
                    SceneNode target = index.nodes().get(edge.target());
                    if (target != null && target.type() == NodeType.GAMEPLAY_GATE) {
                        issues.add(error("IMMEDIATE_GAMEPLAY_GATE", node.id(), "Gameplay gates require LATER_INTERACTION or EXTERNAL_EVENT."));
                    }
                }
                validateActions(edge.actions(), node.id(), project, index, issues);
            }
        } else if (node.type() == NodeType.GAMEPLAY_GATE && (node.next() == null || node.next().isBlank())) {
            issues.add(error("GATE_WITHOUT_TARGET", node.id(), "Gameplay gate requires a next node."));
        }
    }

    private static boolean hasEnding(SceneNode node) {
        return node.type() == NodeType.END
                || node.responses().stream().anyMatch(edge -> edge.transition() == TransitionType.PACK_EXIT);
    }

    private static void validateSpeaker(SceneNode node, List<Diagnostic> issues) {
        if (node.speaker() == null) return;
        if (node.speaker().has("emotion") && !node.speaker().get("emotion").isJsonNull()) {
            if (!node.speaker().get("emotion").isJsonPrimitive()
                    || !node.speaker().get("emotion").getAsJsonPrimitive().isString()
                    || !SpeakerPresentation.validEmotion(node.speaker().get("emotion").getAsString())) {
                issues.add(error("INVALID_SPEAKER_EMOTION", node.id(),
                        "Speaker emotion must be one of " + SpeakerPresentation.EMOTIONS + "."));
            }
        }
        if (node.speaker().has("animation") && !node.speaker().get("animation").isJsonNull()) {
            if (!node.speaker().get("animation").isJsonPrimitive()
                    || !node.speaker().get("animation").getAsJsonPrimitive().isString()
                    || !SpeakerPresentation.validAnimation(node.speaker().get("animation").getAsString())) {
                issues.add(error("INVALID_SPEAKER_ANIMATION", node.id(),
                        "Speaker animation must be one of " + SpeakerPresentation.ANIMATIONS + "."));
            }
        }
    }

    private static void validateCondition(PackCondition condition, String node, ScenePackProject project,
            ProjectIndex index, List<Diagnostic> issues) {
        if (condition == null || condition.type() == null) {
            issues.add(error("INVALID_CONDITION", node, "Condition type is required."));
            return;
        }
        if (condition.type() == ConditionType.RAW) {
            rawAllowed(project, condition.raw(), node, issues);
            return;
        }
        if (condition.type() == ConditionType.SCENE_FILTER) {
            String problem = SceneFilterCatalog.validate(condition.filter());
            if (problem != null) issues.add(error("INVALID_SCENE_FILTER", node, problem));
            return;
        }
        if (condition.type() == ConditionType.HAS_COOKIE || condition.type() == ConditionType.COUNTER) {
            StateType expected = condition.type() == ConditionType.HAS_COOKIE ? StateType.COOKIE : StateType.COUNTER;
            validateStateReference(condition.state(), expected, node, project, index, issues);
        }
        if (Set.of(ConditionType.HAS_ITEM, ConditionType.HELD_ITEM, ConditionType.MOE_HAS_ITEM, ConditionType.BLOCK)
                .contains(condition.type()) && !resource(condition.item())) {
            issues.add(error("INVALID_RESOURCE", node,
                    "Condition requires a valid resource ID in its item field; BLOCK also uses item, not marker."));
        }
    }

    private static void validateActions(List<PackAction> actions, String node, ScenePackProject project,
            ProjectIndex index, List<Diagnostic> issues) {
        for (PackAction action : actions) {
            if (action == null || action.type() == null) {
                issues.add(error("INVALID_ACTION", node, "Action type is required."));
                continue;
            }
            if (action.type() == ActionType.RAW) {
                rawAllowed(project, action.raw(), node, issues);
            } else if (action.type() == ActionType.SET_COOKIE || action.type() == ActionType.DELETE_COOKIE) {
                validateStateReference(action.state(), StateType.COOKIE, node, project, index, issues);
            } else if (action.type() == ActionType.CHANGE_COUNTER) {
                validateStateReference(action.state(), StateType.COUNTER, node, project, index, issues);
                StateDeclaration declaration = index.state().get(action.state());
                if (declaration != null && action.operation() == block_party.conversation.model.ChangeOperation.SET) {
                    if (declaration.minimum() != null && action.amount() < declaration.minimum()
                            || declaration.maximum() != null && action.amount() > declaration.maximum()) {
                        issues.add(error("COUNTER_OUT_OF_BOUNDS", node,
                                "Counter '" + action.state() + "' is set outside its declared bounds."));
                    }
                }
            } else if ((action.type() == ActionType.GIVE_ITEM || action.type() == ActionType.TAKE_ITEM) && !resource(action.item())) {
                issues.add(error("INVALID_RESOURCE", node, "Item action requires a valid item or tag resource id."));
            } else if ((action.type() == ActionType.REMEMBER_LOCATION || action.type() == ActionType.FORGET_LOCATION
                    || action.type() == ActionType.ASSIGN_LOCATION)
                    && (action.location() == null || !validId(action.location()))) {
                issues.add(error("INVALID_LOCATION", node, "Location action requires a lowercase location name."));
            } else if (action.type() == ActionType.ASSIGN_TARGET
                    && !Set.of("owner", "dialogue_player", "social_target", "nearest_moe").contains(action.target())) {
                issues.add(error("INVALID_TARGET", node, "Target assignment requires owner, dialogue_player, social_target, or nearest_moe."));
            } else if (Set.of(ActionType.ASSIGN_LOCATION, ActionType.ASSIGN_TARGET).contains(action.type())
                    && (action.speed() < 0.0D || action.arrivalRadius() < 0.0D || action.timeoutTicks() < 0)) {
                issues.add(error("INVALID_ASSIGNMENT", node, "Assignment speed, arrival radius, and timeout cannot be negative."));
            } else if (action.type() == ActionType.ASSIGN_NEAR_BLOCK && !resource(action.block())) {
                issues.add(error("INVALID_RESOURCE", node, "Block assignment requires a valid block or tag resource ID."));
            } else if (action.type() == ActionType.ASSIGN_NEAR_BLOCK
                    && (action.searchRadius() < 0 || action.searchRadius() > 32
                    || action.verticalRadius() < 0 || action.verticalRadius() > 16)) {
                issues.add(error("INVALID_ASSIGNMENT", node, "Block search radius must be at most 32 and vertical radius at most 16."));
            } else if (action.type() == ActionType.WAIT_RANDOM_TICKS
                    && (action.minTicks() < 0 || action.maxTicks() < action.minTicks())) {
                issues.add(error("INVALID_WAIT", node, "Random wait requires 0 <= minTicks <= maxTicks."));
            } else if (Set.of(ActionType.WAIT_TICKS, ActionType.PLAY_ANIMATION, ActionType.SET_EMOTION).contains(action.type())
                    && action.ticks() < 0) {
                issues.add(error("INVALID_WAIT", node, "Timed action ticks cannot be negative."));
            } else if (action.type() == ActionType.PLAY_ANIMATION && !SpeakerPresentation.validAnimation(action.animation())) {
                issues.add(error("INVALID_ANIMATION", node, "Animation must be one of " + SpeakerPresentation.ANIMATIONS + "."));
            } else if (action.type() == ActionType.SET_EMOTION && !SpeakerPresentation.validEmotion(action.emotion())) {
                issues.add(error("INVALID_EMOTION", node, "Emotion must be one of " + SpeakerPresentation.EMOTIONS + "."));
            }
        }
    }

    private static void validateStateReference(String id, StateType expected, String node, ScenePackProject project,
            ProjectIndex index, List<Diagnostic> issues) {
        StateDeclaration local = index.state().get(id);
        StateReference external = project.contract().requires().stream().filter(value -> value.id().equals(id)).findFirst().orElse(null);
        StateType actual = local == null ? external == null ? null : external.type() : local.type();
        if (actual == null) {
            issues.add(error("UNDECLARED_STATE", node, "State '" + id + "' is neither local nor declared as required."));
        } else if (actual != expected) {
            issues.add(error("STATE_TYPE_MISMATCH", node, "State '" + id + "' is not a " + expected + "."));
        }
    }

    private static void analyzeStateUsage(ScenePackProject project, List<Diagnostic> issues) {
        Set<String> reads = new HashSet<>();
        Set<String> writes = new HashSet<>();
        for (SceneNode node : project.nodes()) {
            node.conditions().stream().map(PackCondition::state).filter(value -> value != null).forEach(reads::add);
            collectWrites(node.actions(), writes);
            node.responses().forEach(edge -> collectWrites(edge.actions(), writes));
        }
        for (StateDeclaration state : project.state()) {
            if (writes.contains(state.id()) && !reads.contains(state.id())) {
                issues.add(warning("STATE_WRITTEN_NOT_READ", null, "State '" + state.id() + "' is written but never read."));
            }
            if (reads.contains(state.id()) && !writes.contains(state.id()) && !initiallyMeaningful(state)) {
                issues.add(warning("STATE_READ_NOT_WRITTEN", null, "State '" + state.id() + "' is read but never written."));
            }
        }
    }

    private static void analyzeRoots(ScenePackProject project, List<Diagnostic> issues) {
        List<SceneNode> roots = new ArrayList<>();
        ProjectIndex index = new ProjectIndex(project);
        roots.add(index.nodes().get(project.entry()));
        project.nodes().stream().filter(node -> node.type() == NodeType.GAMEPLAY_GATE).forEach(roots::add);
        for (int left = 0; left < roots.size(); ++left) {
            for (int right = left + 1; right < roots.size(); ++right) {
                SceneNode a = roots.get(left);
                SceneNode b = roots.get(right);
                if (sameTrigger(a, b) && !mutuallyExclusive(a.conditions(), b.conditions())) {
                    issues.add(warning("AMBIGUOUS_SCENE_SELECTION", a.id(),
                            "May match trigger alongside '" + b.id() + "'; Block Party may choose either at equal specificity."));
                }
            }
        }
    }

    private static void analyzeRewards(ScenePackProject project, Map<String, SceneNode> nodes, List<Diagnostic> issues) {
        for (SceneNode gate : project.nodes()) {
            if (gate.type() != NodeType.GAMEPLAY_GATE || !descendantHasReward(gate.next(), nodes, new HashSet<>())) {
                continue;
            }
            Set<String> guards = new HashSet<>();
            gate.conditions().stream().filter(condition -> condition.type() == ConditionType.HAS_COOKIE && condition.not())
                    .map(PackCondition::state).forEach(guards::add);
            Set<String> writes = new HashSet<>();
            collectWrites(gate.actions(), writes);
            if (guards.stream().noneMatch(writes::contains)) {
                issues.add(error("REPEATABLE_REWARD", gate.id(),
                        "Declare a COOKIE state, add not=true HAS_COOKIE for it to this GAMEPLAY_GATE, and add SET_COOKIE "
                                + "for the same state directly to this gate's actions. Counter guards are not accepted."));
            }
        }
    }

    private static boolean descendantHasReward(String id, Map<String, SceneNode> nodes, Set<String> seen) {
        SceneNode node = nodes.get(id);
        if (node == null || !seen.add(id)) {
            return false;
        }
        if (node.actions().stream().anyMatch(action -> action.type() == ActionType.GIVE_ITEM)) {
            return true;
        }
        return node.responses().stream().anyMatch(edge -> edge.actions().stream().anyMatch(action -> action.type() == ActionType.GIVE_ITEM)
                || descendantHasReward(edge.target(), nodes, seen));
    }

    private static boolean mutuallyExclusive(List<PackCondition> a, List<PackCondition> b) {
        for (PackCondition left : a) {
            if (left.type() != ConditionType.HAS_COOKIE) continue;
            for (PackCondition right : b) {
                if (right.type() == ConditionType.HAS_COOKIE && java.util.Objects.equals(left.state(), right.state())
                        && left.not() != right.not()) return true;
            }
        }
        return false;
    }

    private static boolean sameTrigger(SceneNode a, SceneNode b) {
        return java.util.Objects.equals(trigger(a), trigger(b));
    }

    private static String trigger(SceneNode node) {
        return node.trigger() == null || node.trigger().isBlank() ? "block_party:right_click" : node.trigger();
    }

    private static void collectWrites(List<PackAction> actions, Set<String> writes) {
        actions.stream().filter(action -> Set.of(ActionType.SET_COOKIE, ActionType.DELETE_COOKIE, ActionType.CHANGE_COUNTER).contains(action.type()))
                .map(PackAction::state).filter(value -> value != null).forEach(writes::add);
    }

    private static boolean initiallyMeaningful(StateDeclaration state) {
        return state.type() == StateType.COOKIE ? state.initialCookie() : state.initialCounter() != 0;
    }

    private static void rawAllowed(ScenePackProject project, Object raw, String node, List<Diagnostic> issues) {
        if (!project.allowRawMechanics()) {
            issues.add(error("RAW_MECHANIC_DISABLED", node, "Raw mechanics require allowRawMechanics=true."));
        } else if (raw == null) {
            issues.add(error("EMPTY_RAW_MECHANIC", node, "Raw mechanic payload is required."));
        } else {
            issues.add(warning("RAW_MECHANIC", node, "Raw mechanic cannot be simulated or deeply validated."));
        }
    }

    private static List<String> targets(SceneNode node) {
        List<String> targets = new ArrayList<>();
        if (node.next() != null && !node.next().isBlank()) targets.add(node.next());
        node.responses().stream().map(ResponseEdge::target).filter(value -> value != null && !value.isBlank()).forEach(targets::add);
        return targets;
    }

    private static Set<String> reachable(String entry, Map<String, SceneNode> nodes) {
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> pending = new ArrayDeque<>();
        pending.add(entry);
        while (!pending.isEmpty()) {
            String id = pending.removeFirst();
            if (!visited.add(id)) continue;
            SceneNode node = nodes.get(id);
            if (node != null) targets(node).stream().filter(nodes::containsKey).forEach(pending::addLast);
        }
        return visited;
    }

    private static boolean validId(String value) {
        return value != null && ID.matcher(value).matches();
    }

    private static boolean resource(String value) {
        return value != null && RESOURCE.matcher(value).matches();
    }

    private static String unqualify(String value) {
        if (value == null) return "";
        int separator = value.indexOf(':');
        return separator < 0 ? value : value.substring(separator + 1);
    }

    private static Diagnostic error(String code, String node, String message) {
        return new Diagnostic(Severity.ERROR, code, node, message);
    }

    private static Diagnostic warning(String code, String node, String message) {
        return new Diagnostic(Severity.WARNING, code, node, message);
    }
}
