package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import block_party.scene.SceneObservation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimesBlockEntity extends LocativeBlockEntity {
    public WindChimesBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.WIND_CHIME.get(), pos, state);
    }

    @Override
    public SceneObservation getRequiredCondition() {
        return SceneObservation.NEVER;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
