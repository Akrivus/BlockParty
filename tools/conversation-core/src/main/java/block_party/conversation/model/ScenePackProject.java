package block_party.conversation.model;

import java.util.List;

public record ScenePackProject(
        int projectFormat,
        ProjectTarget target,
        PackMetadata pack,
        PackContract contract,
        List<StateDeclaration> state,
        boolean allowRawMechanics,
        String entry,
        List<SceneNode> nodes) {
    public ScenePackProject {
        contract = contract == null ? new PackContract(null, null, null) : contract;
        state = state == null ? List.of() : List.copyOf(state);
        nodes = nodes == null ? List.of() : List.copyOf(nodes);
    }
}
