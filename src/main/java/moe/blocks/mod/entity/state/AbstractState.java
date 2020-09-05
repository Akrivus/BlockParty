package moe.blocks.mod.entity.state;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.dating.Relationship;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractState implements IMachineState {
    protected FiniteEntity entity;

    @Override
    public void start(FiniteEntity entity) {
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

    public void setStateEntity(FiniteEntity entity) {
        this.entity = entity;
    }

    public void onHello(LivingEntity host, Relationship relationship) {

    }

    public void onStare(LivingEntity host, Relationship relationship) {

    }
}
