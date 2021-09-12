package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.db.records.Shrine;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ToriiTabletBlockEntity extends AbstractDataBlockEntity<Shrine> {
    public ToriiTabletBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.TORII_TABLET.get(), pos, state);
    }

    @Override
    public Shrine getRow() {
        return BlockPartyDB.Shrines.find(this.getDatabaseID());
    }

    @Override
    public Shrine getNewRow() {
        return new Shrine(this);
    }
}
