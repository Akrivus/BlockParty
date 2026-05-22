package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ShrineTabletBlockEntity extends AbstractDataBlockEntity {
    public ShrineTabletBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.SHRINE_TABLET.get(), pos, state);
    }

    @Override
    public String getTableName() {
        return "Shrines";
    }
}
