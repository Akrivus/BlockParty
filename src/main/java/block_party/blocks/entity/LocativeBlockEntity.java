package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class LocativeBlockEntity extends AbstractDataBlockEntity {
    protected LocativeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public String getTableName() {
        return BlockPartyDB.TABLE_LOCATIONS;
    }
}
