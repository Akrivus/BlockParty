package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.db.records.Gathering;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PaperLanternBlockEntity extends AbstractDataBlockEntity<Gathering> {
    public PaperLanternBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.PAPER_LANTERN.get(), pos, state);
    }

    @Override
    public Gathering getRow() {
        return BlockPartyDB.Gatherings.find(this.getDatabaseID());
    }

    @Override
    public Gathering getNewRow() {
        return new Gathering(this);
    }
}
