package block_party.conversation.model;

public record SceneSelection(String group, int weight, int cooldownTicks) {
    public SceneSelection {
        group = group == null ? "" : group;
    }
}
