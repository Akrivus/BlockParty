package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import block_party.scene.SceneFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimesBlockEntity extends LocativeBlockEntity {
    public WindChimesBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.WIND_CHIME.get(), pos, state);
    }

    @Override
    public SceneFilter getRequiredCondition() {
        return SceneFilter.NEVER;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
