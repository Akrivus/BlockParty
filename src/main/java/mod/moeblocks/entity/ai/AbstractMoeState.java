package mod.moeblocks.entity.ai;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.StateEntity;

public abstract class AbstractMoeState extends AbstractState {
    protected MoeEntity moe;

    @Override
    public void start(StateEntity entity) {
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
