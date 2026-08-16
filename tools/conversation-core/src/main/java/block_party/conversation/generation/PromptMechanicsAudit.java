package block_party.conversation.generation;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.PackAction;
import block_party.conversation.model.PackCondition;
import block_party.conversation.model.ResponseEdge;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.model.TransitionType;
import block_party.conversation.validation.Diagnostic;
import block_party.conversation.validation.Severity;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Predicate;

/** Checks high-confidence prompt mechanics so valid prose cannot silently replace requested gameplay. */
final class PromptMechanicsAudit {
    private PromptMechanicsAudit() {}

    static List<Diagnostic> audit(GenerationBrief brief, ScenePackProject project) {
        String prompt = brief.prompt() == null ? "" : brief.prompt().toLowerCase(Locale.ROOT);
        List<Diagnostic> issues = new ArrayList<>();
        expectFilter(prompt, project, issues, List.of("sunset"), "time_period", "evening", "sunset/evening");
        expectFilter(prompt, project, issues, List.of("clear weather", "if it is clear", "if it's clear"),
                "weather", "clear", "clear weather");
        expectFilter(prompt, project, issues, List.of("moe is idle", "moe's idle", "idle moe"),
                "routine_intent", "idle", "idle routine state");
        expectFilter(prompt, project, issues, List.of("at dawn"), "time_period", "dawn", "dawn");
        expectFilter(prompt, project, issues, List.of("at noon"), "time_period", "noon", "noon");
        expectFilter(prompt, project, issues, List.of("at midnight"), "time_period", "midnight", "midnight");
        expectFilter(prompt, project, issues, List.of("if it is raining", "if it's raining", "in the rain"),
                "weather", "rain", "rain");
        expectFilter(prompt, project, issues, List.of("during a thunderstorm", "if it is thundering", "if it's thundering"),
                "weather", "thunder", "thunder");

        boolean autonomous = contains(prompt, "moe is idle", "idle moe", "routine", "on its own", "autonomous", "background behavior");
        boolean triggerLocked = brief.lockedTrigger() != null && !brief.lockedTrigger().isBlank();
        if (autonomous && !triggerLocked && project.nodes().stream().noneMatch(node -> trigger(node, "routine_tick"))) {
            issues.add(error("MISSING_PROMPT_TRIGGER", "Prompt requests autonomous behavior; use routine_tick."));
        }
        requireAction(prompt, project, issues, List.of("walk to", "go to", "return home"),
                action -> action.type() == ActionType.ASSIGN_LOCATION, "ASSIGN_LOCATION");
        requireAction(prompt, project, issues, List.of("look at"),
                action -> action.type() == ActionType.LOOK_AT_ASSIGNMENT, "LOOK_AT_ASSIGNMENT");
        requireAction(prompt, project, issues, List.of("awe animation", "play awe"),
                action -> action.type() == ActionType.PLAY_ANIMATION && "AWE".equalsIgnoreCase(action.animation()),
                "PLAY_ANIMATION with AWE");
        requireAction(prompt, project, issues, List.of("wait briefly", "pause briefly", "wait a moment"),
                action -> action.type() == ActionType.WAIT_TICKS || action.type() == ActionType.WAIT_RANDOM_TICKS,
                "WAIT_TICKS or WAIT_RANDOM_TICKS");
        if (contains(prompt, "look at the lantern", "walk to the lantern")
                && !hasAction(project, action -> action.type() == ActionType.ASSIGN_NEAR_BLOCK)) {
            issues.add(error("MISSING_PROMPT_ACTION", "Prompt targets a lantern block; use ASSIGN_NEAR_BLOCK before looking at it."));
        }
        long locationAssignments = actions(project).stream().filter(action -> action.type() == ActionType.ASSIGN_LOCATION).count();
        if (prompt.contains("return home") && locationAssignments < 2) {
            issues.add(error("MISSING_PROMPT_RETURN", "Prompt requests travel and a return home; use separate outbound and home ASSIGN_LOCATION actions."));
        }
        boolean assignmentRequested = contains(prompt, "walk to", "go to", "return home");
        boolean stagingRequested = contains(prompt, "look at", " animation", "wait briefly", "pause briefly");
        if (assignmentRequested && stagingRequested
                && project.nodes().stream().noneMatch(node -> trigger(node, "assignment_arrived"))) {
            issues.add(error("MISSING_ASSIGNMENT_CHOREOGRAPHY",
                    "Movement is asynchronous. Continue look/animation/wait actions from assignment_arrived nodes keyed by assignment ID."));
        }
        boolean startsFollow = hasAction(project, action -> action.type() == ActionType.START_FOLLOW);
        boolean assignsRoute = hasAction(project, PromptMechanicsAudit::assignment);
        if (startsFollow && assignsRoute) {
            issues.add(error("CONFLICTING_MOVEMENT_MODES",
                    "START_FOLLOW suppresses assignment movement. Remove follow mode from the route or clear it before assigning a destination."));
        }
        if (!contains(prompt, "later interaction", "next time", "when they talk again")) {
            Map<String, SceneNode> nodes = project.nodes().stream().collect(Collectors.toMap(SceneNode::id, node -> node));
            for (SceneNode node : project.nodes()) for (ResponseEdge response : node.responses()) {
                SceneNode target = nodes.get(response.target());
                if (response.transition() == TransitionType.LATER_INTERACTION && target != null
                        && target.actions().stream().anyMatch(PromptMechanicsAudit::assignment)) {
                    issues.add(new Diagnostic(Severity.ERROR, "DEFERRED_ASSIGNMENT_START", node.id(),
                            "The accepted response defers its assignment gate until another right-click. Put the assignment "
                                    + "on the response and use EXTERNAL_EVENT toward its assignment_arrived node."));
                }
            }
        }
        return List.copyOf(issues);
    }

    private static boolean assignment(PackAction action) {
        return action.type() == ActionType.ASSIGN_LOCATION || action.type() == ActionType.ASSIGN_TARGET
                || action.type() == ActionType.ASSIGN_NEAR_BLOCK;
    }

    private static void expectFilter(String prompt, ScenePackProject project, List<Diagnostic> issues,
            List<String> phrases, String type, String value, String label) {
        if (phrases.stream().noneMatch(prompt::contains) || hasFilter(project, type, value)) return;
        issues.add(error("MISSING_PROMPT_FILTER", "Prompt requests " + label
                + "; add SCENE_FILTER block_party:" + type + " with value '" + value + "'."));
    }

    private static void requireAction(String prompt, ScenePackProject project, List<Diagnostic> issues,
            List<String> phrases, Predicate<PackAction> predicate, String label) {
        if (phrases.stream().noneMatch(prompt::contains) || hasAction(project, predicate)) return;
        issues.add(error("MISSING_PROMPT_ACTION", "Prompt explicitly requests behavior requiring " + label + "."));
    }

    private static boolean hasFilter(ScenePackProject project, String type, String value) {
        for (SceneNode node : project.nodes()) for (PackCondition condition : node.conditions()) {
            JsonObject filter = condition.type() == ConditionType.SCENE_FILTER ? condition.filter() : null;
            if (filter == null || !filter.has("type")) continue;
            String actualType = filter.get("type").getAsString().replaceFirst("^.*:", "");
            String actualValue = filter.has("value") ? filter.get("value").getAsString() : "";
            if (type.equals(actualType) && value.equalsIgnoreCase(actualValue)) return true;
        }
        return false;
    }

    private static boolean hasAction(ScenePackProject project, Predicate<PackAction> predicate) {
        return actions(project).stream().anyMatch(predicate);
    }

    private static List<PackAction> actions(ScenePackProject project) {
        List<PackAction> actions = new ArrayList<>();
        for (SceneNode node : project.nodes()) {
            actions.addAll(node.actions());
            for (ResponseEdge response : node.responses()) actions.addAll(response.actions());
        }
        return actions;
    }

    private static boolean trigger(SceneNode node, String expected) {
        if (node.trigger() == null) return false;
        return expected.equals(node.trigger().replaceFirst("^.*:", ""));
    }

    private static boolean contains(String value, String... phrases) {
        for (String phrase : phrases) if (value.contains(phrase)) return true;
        return false;
    }

    private static Diagnostic error(String code, String message) {
        return new Diagnostic(Severity.ERROR, code, null, message);
    }
}
