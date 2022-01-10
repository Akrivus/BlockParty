package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import block_party.scene.SceneRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class HangingScrollBlockEntity extends LocativeBlockEntity {
    protected SceneRequirement condition;

    public HangingScrollBlockEntity(BlockPos pos, BlockState state) {
        this(SceneRequirement.NEVER, pos, state);
    }

    public HangingScrollBlockEntity(SceneRequirement condition, BlockPos pos, BlockState state) {
        super(CustomBlockEntities.HANGING_SCROLL.get(), pos, state);
        this.condition = condition;
    }

    @Override
    public void load(CompoundTag compound) {
        this.condition = SceneRequirement.valueOf(compound.getString("Condition"));
        super.load(compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putString("Condition", this.condition.name());
        return super.save(compound);
    }

    @Override
    public SceneRequirement getRequiredCondition() {
        return this.condition;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
