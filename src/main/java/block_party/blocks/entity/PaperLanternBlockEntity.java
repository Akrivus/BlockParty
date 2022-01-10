package block_party.blocks.entity;

import block_party.scene.SceneRequirement;
import block_party.registry.CustomBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PaperLanternBlockEntity extends LocativeBlockEntity {
    public PaperLanternBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.PAPER_LANTERN.get(), pos, state);
    }

    @Override
    public SceneRequirement getRequiredCondition() {
        return SceneRequirement.ALWAYS;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
