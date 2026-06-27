package block_party.world.attention;

import java.util.List;

public final class AttentionDefinitions {
    private static final List<AttentionDefinition> ALL = List.of(
            WoodForestAttention.OAK,
            WoodForestAttention.BIRCH,
            WoodForestAttention.SPRUCE,
            WoodForestAttention.JUNGLE,
            WoodForestAttention.ACACIA,
            WoodForestAttention.DARK_OAK);

    private AttentionDefinitions() {
    }

    public static List<AttentionDefinition> all() {
        return ALL;
    }
}
