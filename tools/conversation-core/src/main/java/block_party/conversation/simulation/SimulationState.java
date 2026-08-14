package block_party.conversation.simulation;

import block_party.conversation.model.StateDeclaration;
import block_party.conversation.model.StateType;
import java.util.HashMap;
import java.util.Map;

final class SimulationState {
    final Map<String, Boolean> cookies = new HashMap<>();
    final Map<String, Integer> counters = new HashMap<>();
    final Map<String, Integer> inventory = new HashMap<>();

    SimulationState(Iterable<StateDeclaration> declarations) {
        for (StateDeclaration state : declarations) {
            if (state.type() == StateType.COOKIE) cookies.put(state.id(), state.initialCookie());
            if (state.type() == StateType.COUNTER) counters.put(state.id(), state.initialCounter());
        }
    }

    void apply(SimulationScenario scenario) {
        cookies.putAll(scenario.cookies());
        counters.putAll(scenario.counters());
        inventory.putAll(scenario.inventory());
    }

    private SimulationState() {
    }

    SimulationState copy() {
        SimulationState result = new SimulationState();
        result.cookies.putAll(cookies);
        result.counters.putAll(counters);
        result.inventory.putAll(inventory);
        return result;
    }
}
