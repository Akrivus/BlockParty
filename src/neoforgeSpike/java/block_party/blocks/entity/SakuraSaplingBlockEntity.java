package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SakuraSaplingBlockEntity extends AbstractDataBlockEntity {
    public SakuraSaplingBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.SAKURA_SAPLING.get(), pos, state);
    }

    @Override
    public String getTableName() {
        return "SakuraSaplings";
    }
}
