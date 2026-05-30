package block_party.world.attention;

import java.util.List;

public final class AttentionDefinitions {
    private static final List<AttentionDefinition> ALL = List.of(
            OakForestAttention.INSTANCE);

    private AttentionDefinitions() {
    }

    public static List<AttentionDefinition> all() {
        return ALL;
    }
}
