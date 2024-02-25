package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import block_party.scene.SceneObservation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class HangingScrollBlockEntity extends LocativeBlockEntity {
    protected SceneObservation condition;

    public HangingScrollBlockEntity(BlockPos pos, BlockState state) {
        this(SceneObservation.NEVER, pos, state);
    }

    public HangingScrollBlockEntity(SceneObservation condition, BlockPos pos, BlockState state) {
        super(CustomBlockEntities.HANGING_SCROLL.get(), pos, state);
        this.condition = condition;
    }

    @Override
    public void load(CompoundTag compound) {
        this.condition = SceneObservation.valueOf(compound.getString("Condition"));
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putString("Condition", this.condition.name());
        super.saveAdditional(compound);
    }

    @Override
    public SceneObservation getRequiredCondition() {
        return this.condition;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
