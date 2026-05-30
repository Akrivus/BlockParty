package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.registry.CustomBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ShimenawaBlockEntity extends AbstractDataBlockEntity {
    public ShimenawaBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.SHIMENAWA.get(), pos, state);
    }

    @Override
    public String getTableName() {
        return BlockPartyDB.TABLE_NPCS;
    }
}
