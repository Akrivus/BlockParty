package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import block_party.db.BlockPartyDB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GardenLanternBlockEntity extends AbstractDataBlockEntity {
    public GardenLanternBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.GARDEN_LANTERN.get(), pos, state);
    }

    @Override
    public String getTableName() {
        return BlockPartyDB.TABLE_GARDEN_LANTERNS;
    }
}
