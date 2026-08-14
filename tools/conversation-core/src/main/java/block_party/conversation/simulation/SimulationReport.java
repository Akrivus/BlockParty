package block_party.conversation.simulation;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Set;

public record SimulationReport(
        int routes,
        Set<String> endings,
        Set<String> gameplayGates,
        Set<String> externalRequirements,
        List<String> cycles,
        List<List<String>> traces) {
    public SimulationReport {
        endings = Collections.unmodifiableSet(new LinkedHashSet<>(endings));
        gameplayGates = Collections.unmodifiableSet(new LinkedHashSet<>(gameplayGates));
        externalRequirements = Collections.unmodifiableSet(new LinkedHashSet<>(externalRequirements));
        cycles = List.copyOf(cycles);
        traces = traces.stream().map(List::copyOf).toList();
    }
}
