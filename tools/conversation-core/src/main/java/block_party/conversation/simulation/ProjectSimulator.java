package block_party.conversation.simulation;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ChangeOperation;
import block_party.conversation.model.Comparison;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.NodeType;
import block_party.conversation.model.PackAction;
import block_party.conversation.model.PackCondition;
import block_party.conversation.model.ProjectIndex;
import block_party.conversation.model.ResponseEdge;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.ScenePackProject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ProjectSimulator {
    public SimulationReport simulate(ScenePackProject project) {
        return simulate(project, new SimulationScenario(null, null, null));
    }

    public SimulationReport simulate(ScenePackProject project, SimulationScenario scenario) {
        ProjectIndex index = new ProjectIndex(project);
        Result result = new Result();
        SimulationState initial = new SimulationState(project.state());
        initial.apply(scenario);
        walk(project.entry(), index, initial, new ArrayList<>(), new ArrayList<>(), result);
        return new SimulationReport(result.routes, result.endings, result.gates, result.external, result.cycles, result.traces);
    }

    private static void walk(String id, ProjectIndex index, SimulationState state, List<String> path,
            List<String> trace, Result result) {
        SceneNode node = index.nodes().get(id);
        if (node == null) return;
        if (path.contains(id)) {
            result.cycles.add(String.join(" -> ", path) + " -> " + id);
            return;
        }
        List<String> nextPath = new ArrayList<>(path);
        nextPath.add(id);
        List<String> nextTrace = new ArrayList<>(trace);
        nextTrace.add("enter " + id);
        if (!satisfy(node.conditions(), state, nextTrace, result)) {
            nextTrace.add("blocked " + id);
            return;
        }
        apply(node.actions(), state, nextTrace);
        if (node.type() == NodeType.END) {
            ++result.routes;
            String ending = node.ending() == null || node.ending().isBlank() ? node.id() : node.ending();
            result.endings.add(ending);
            nextTrace.add("ending " + ending);
            result.traces.add(nextTrace);
            return;
        }
        if (node.type() == NodeType.GAMEPLAY_GATE) {
            result.gates.add(node.id());
            walk(node.next(), index, state, nextPath, nextTrace, result);
            return;
        }
        for (ResponseEdge edge : node.responses()) {
            SimulationState branch = state.copy();
            List<String> branchTrace = new ArrayList<>(nextTrace);
            branchTrace.add("choose " + edge.cue() + (edge.label() == null ? "" : " (" + edge.label() + ")"));
            apply(edge.actions(), branch, branchTrace);
            walk(edge.target(), index, branch, nextPath, branchTrace, result);
        }
    }

    private static boolean satisfy(List<PackCondition> conditions, SimulationState state, List<String> trace, Result result) {
        for (PackCondition condition : conditions) {
            boolean pass = switch (condition.type()) {
                case ALWAYS -> true;
                case HAS_COOKIE -> state.cookies.getOrDefault(condition.state(), false);
                case COUNTER -> compare(state.counters.getOrDefault(condition.state(), 0), condition.value(), condition.comparison());
                case HAS_ITEM, HELD_ITEM, MOE_HAS_ITEM -> {
                    int required = Math.max(1, condition.count());
                    if (state.inventory.getOrDefault(condition.item(), 0) < required) {
                        state.inventory.put(condition.item(), required);
                        String requirement = required + " x " + condition.item();
                        result.external.add(requirement);
                        trace.add("external acquire " + requirement);
                    }
                    yield true;
                }
                case BLOCK, ELAPSED_TIME, RAW -> {
                    result.external.add(condition.type().name().toLowerCase() + " condition");
                    yield true;
                }
            };
            if (condition.not()) pass = !pass;
            if (!pass) return false;
        }
        return true;
    }

    private static void apply(List<PackAction> actions, SimulationState state, List<String> trace) {
        for (PackAction action : actions) {
            switch (action.type()) {
                case SET_COOKIE -> {
                    boolean value = action.value() == null || Boolean.parseBoolean(action.value());
                    state.cookies.put(action.state(), value);
                    trace.add(action.state() + " = " + value);
                }
                case DELETE_COOKIE -> {
                    state.cookies.put(action.state(), false);
                    trace.add(action.state() + " = false");
                }
                case CHANGE_COUNTER -> {
                    int before = state.counters.getOrDefault(action.state(), 0);
                    int after = switch (action.operation() == null ? ChangeOperation.ADD : action.operation()) {
                        case ADD -> before + action.amount();
                        case SUBTRACT -> before - action.amount();
                        case SET -> action.amount();
                        case DELETE -> 0;
                    };
                    state.counters.put(action.state(), after);
                    trace.add(action.state() + " = " + after);
                }
                case GIVE_ITEM -> {
                    state.inventory.merge(action.item(), Math.max(1, action.count()), Integer::sum);
                    trace.add("give " + Math.max(1, action.count()) + " x " + action.item());
                }
                case TAKE_ITEM -> {
                    state.inventory.merge(action.item(), -Math.max(1, action.count()), Integer::sum);
                    trace.add("take " + Math.max(1, action.count()) + " x " + action.item());
                }
                case MARK_TIME -> trace.add("mark time " + action.marker());
                case OPEN_INVENTORY, START_FOLLOW, CLEAR_FOLLOW, END, RAW -> trace.add("action " + action.type().name().toLowerCase());
            }
        }
    }

    private static boolean compare(int actual, int expected, Comparison comparison) {
        return switch (comparison == null ? Comparison.EQUALS : comparison) {
            case EQUALS -> actual == expected;
            case NOT_EQUALS -> actual != expected;
            case GREATER_THAN -> actual > expected;
            case GREATER_OR_EQUAL -> actual >= expected;
            case LESS_THAN -> actual < expected;
            case LESS_OR_EQUAL -> actual <= expected;
        };
    }

    private static final class Result {
        private int routes;
        private final Set<String> endings = new LinkedHashSet<>();
        private final Set<String> gates = new LinkedHashSet<>();
        private final Set<String> external = new LinkedHashSet<>();
        private final List<String> cycles = new ArrayList<>();
        private final List<List<String>> traces = new ArrayList<>();
    }
}
