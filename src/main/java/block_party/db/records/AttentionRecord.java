package block_party.db.records;

import block_party.db.DimBlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public record AttentionRecord(
        long databaseId,
        UUID playerUuid,
        String type,
        String source,
        DimBlockPos dimPos,
        BlockState blockState,
        String itemId,
        int itemCount,
        int count,
        long firstGameTime,
        long lastGameTime) {
}
