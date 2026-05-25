package block_party.db.records;

import block_party.db.DimBlockPos;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public record TsukumogamiCandidate(
        long databaseId,
        DimBlockPos dimPos,
        UUID playerUuid,
        BlockState blockState,
        CompoundTag tileEntityData,
        long createdGameTime,
        long matureAtGameTime,
        long shrineDatabaseId) {
}
