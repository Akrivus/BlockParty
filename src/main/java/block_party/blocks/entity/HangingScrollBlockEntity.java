package block_party.blocks.entity;

import block_party.custom.CustomBlockEntities;
import block_party.npc.automata.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class HangingScrollBlockEntity extends LocativeBlockEntity {
    protected Condition condition;

    public HangingScrollBlockEntity(Condition condition, BlockPos pos, BlockState state) {
        super(CustomBlockEntities.HANGING_SCROLL.get(), pos, state);
        this.condition = condition;
    }

    public HangingScrollBlockEntity(BlockPos pos, BlockState state) {
        this(Condition.NEVER, pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        this.condition = Condition.valueOf(compound.getString("Condition"));
        super.load(compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putString("Condition", this.condition.name());
        return super.save(compound);
    }

    @Override
    public Condition getRequiredCondition() {
        return this.condition;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
