package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import block_party.scene.SceneRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimesBlockEntity extends LocativeBlockEntity {
    public WindChimesBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.WIND_CHIME.get(), pos, state);
    }

    @Override
    public SceneRequirement getRequiredCondition() {
        return SceneRequirement.NEVER;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
