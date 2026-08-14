package block_party.conversation.model;

import java.util.List;

public record PackContract(List<StateReference> requires, List<StateReference> provides, List<String> outcomes) {
    public PackContract {
        requires = requires == null ? List.of() : List.copyOf(requires);
        provides = provides == null ? List.of() : List.copyOf(provides);
        outcomes = outcomes == null ? List.of() : List.copyOf(outcomes);
    }
}
