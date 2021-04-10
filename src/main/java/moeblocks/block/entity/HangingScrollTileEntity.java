package moeblocks.block.entity;

import moeblocks.automata.Condition;
import moeblocks.data.HangingScroll;
import moeblocks.init.MoeTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class HangingScrollTileEntity extends AbstractDataTileEntity<HangingScroll> {
    protected Condition condition;

    public HangingScrollTileEntity(Condition condition) {
        super(MoeTileEntities.HANGING_SCROLL.get());
        this.condition = condition;
    }

    public HangingScrollTileEntity() {
        this(Condition.NEVER);
    }

    public Condition getSymbol() {
        return this.condition;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        this.condition = Condition.valueOf(compound.getString("Condition"));
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putString("Condition", this.condition.name());
        return super.write(compound);
    }

    @Override
    public HangingScroll getRow() {
        return null;
    }

    @Override
    public HangingScroll getNewRow() {
        return null;
    }
}
