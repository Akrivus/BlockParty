package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.db.records.Garden;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GardenLanternBlockEntity extends AbstractDataBlockEntity<Garden> {
    public GardenLanternBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.GARDEN_LANTERN.get(), pos, state);
    }

    @Override
    public Garden getRow() {
        return BlockPartyDB.Gardens.find(this.getDatabaseID());
    }

    @Override
    public Garden getNewRow() {
        return new Garden(this);
    }
}
