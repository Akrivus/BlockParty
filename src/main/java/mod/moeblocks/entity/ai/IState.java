package mod.moeblocks.entity.ai;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IState {
    void start(MoeEntity moe);

    IState stop(IState swap);

    void start();

    void tick();

    void stop();

    void read(CompoundNBT compound);

    void write(CompoundNBT compound);

    String toString();
}
