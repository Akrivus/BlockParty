package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WritingTableBlockEntity extends AbstractDataBlockEntity<WritingTable> {
    public WritingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.WRITING_TABLE.get(), pos, state);
    }

    @Override
    public WritingTable getRow() {
        return BlockPartyDB.WritingTables.find(this.getDatabaseID());
    }

    @Override
    public WritingTable getNewRow() {
        return new WritingTable(this);
    }
}
