package moe.blocks.mod.entity.state;

import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.FiniteEntity;

public abstract class AbstractMoeState extends AbstractState {
    protected MoeEntity moe;

    @Override
    public void start(FiniteEntity entity) {
        this.moe = (MoeEntity) entity;
        this.start();
    }

    @Override
    public IMachineState stop(IMachineState swap) {
        this.stop();
        swap.start(this.moe);
        return swap;
    }

    public void setMoe(MoeEntity moe) {
        this.setStateEntity(this.moe = moe);
    }
}
