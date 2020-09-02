package moeblocks.mod.entity.ai;

import moeblocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractState implements IMachineState {
    protected StudentEntity entity;

    @Override
    public void start(StudentEntity entity) {
        this.entity = entity;
        this.start();
    }

    @Override
    public IMachineState stop(IMachineState swap) {
        this.stop();
        swap.start(this.entity);
        return swap;
    }

    @Override
    public Enum<?> getKey() {
        return null;
    }

    @Override
    public boolean matches(Enum<?>... keys) {
        return false;
    }

    public void setStateEntity(StudentEntity entity) {
        this.entity = entity;
    }

    public void onHello(LivingEntity host, Relationship relationship) {

    }

    public void onStare(LivingEntity host, Relationship relationship) {

    }
}
