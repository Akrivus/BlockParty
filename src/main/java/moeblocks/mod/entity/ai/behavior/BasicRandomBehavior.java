package moeblocks.mod.entity.ai.behavior;

import net.minecraft.nbt.CompoundNBT;

public class BasicRandomBehavior extends BasicBehavior {
    protected int timeUntilRandom;

    @Override
    public void start() {
        this.setInterval();
    }

    @Override
    public void tick() {
        if (--this.timeUntilRandom < 0) {
            this.onRandomTick();
            this.setInterval();
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        this.timeUntilRandom = compound.getInt("TimeUntilRandom");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putInt("TimeUntilRandom", this.timeUntilRandom);
    }

    public void onRandomTick() {

    }

    public void setInterval() {
        this.timeUntilRandom = this.getInterval();
    }

    public int getInterval() {
        return this.moe.world.rand.nextInt(300) + 300;
    }
}
