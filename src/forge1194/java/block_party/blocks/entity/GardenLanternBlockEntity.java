package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.db.records.Garden;
import block_party.registry.CustomBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GardenLanternBlockEntity extends AbstractDataBlockEntity<Garden> {
    public GardenLanternBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.GARDEN_LANTERN.get(), pos, state);
    }

    @Override
    public Garden getNewRow() {
        return new Garden(this);
    }

    @Override
    public Garden getRow() {
        return BlockPartyDB.Gardens.find(this.getDatabaseID());
    }
}
