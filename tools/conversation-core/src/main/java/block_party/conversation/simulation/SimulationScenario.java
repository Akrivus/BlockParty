package block_party.conversation.simulation;

import java.util.Map;

public record SimulationScenario(Map<String, Boolean> cookies, Map<String, Integer> counters, Map<String, Integer> inventory) {
    public SimulationScenario {
        cookies = cookies == null ? Map.of() : Map.copyOf(cookies);
        counters = counters == null ? Map.of() : Map.copyOf(counters);
        inventory = inventory == null ? Map.of() : Map.copyOf(inventory);
    }
}
