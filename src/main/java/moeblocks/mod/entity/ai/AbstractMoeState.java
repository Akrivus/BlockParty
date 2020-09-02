package moeblocks.mod.entity.ai;

import moeblocks.mod.entity.MoeEntity;
import moeblocks.mod.entity.StudentEntity;

public abstract class AbstractMoeState extends AbstractState {
    protected MoeEntity moe;

    @Override
    public void start(StudentEntity entity) {
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
