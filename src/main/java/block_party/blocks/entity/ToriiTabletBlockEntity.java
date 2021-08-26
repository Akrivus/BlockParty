package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.db.records.Party;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ToriiTabletBlockEntity extends AbstractDataBlockEntity<Party> {
    public ToriiTabletBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.TORII_TABLET.get(), pos, state);
    }

    @Override
    public Party getRow() {
        return BlockPartyDB.Parties.find(this.getDatabaseID());
    }

    @Override
    public Party getNewRow() {
        return new Party(this);
    }
}
