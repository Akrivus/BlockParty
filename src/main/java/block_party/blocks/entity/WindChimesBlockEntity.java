package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimesBlockEntity extends AbstractDataBlockEntity<WindChimes> {
    public WindChimesBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.WIND_CHIME.get(), pos, state);
    }

    @Override
    public WindChimes getRow() {
        return BlockPartyDB.WindChimes.find(this.getDatabaseID());
    }

    @Override
    public WindChimes getNewRow() {
        return new WindChimes(this);
    }
}
