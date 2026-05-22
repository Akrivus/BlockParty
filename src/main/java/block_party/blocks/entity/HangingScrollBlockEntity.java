package block_party.blocks.entity;

import block_party.registry.CustomBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class HangingScrollBlockEntity extends LocativeBlockEntity {
    private String condition = "NEVER";

    public HangingScrollBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.HANGING_SCROLL.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (compound.contains("Condition")) {
            this.condition = compound.getString("Condition");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        compound.putString("Condition", this.condition);
        super.saveAdditional(compound, provider);
    }

    @Override
    public String getRequiredCondition() {
        return this.condition;
    }

    public void setRequiredCondition(String condition) {
        this.condition = condition == null || condition.isBlank() ? "NEVER" : condition;
        this.setChanged();
    }
}
