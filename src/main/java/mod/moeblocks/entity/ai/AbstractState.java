package mod.moeblocks.entity.ai;

import mod.moeblocks.entity.MoeEntity;

public abstract class AbstractState implements IMachineState {
    protected MoeEntity moe;

    @Override
    public void start(MoeEntity moe) {
        this.moe = moe;
        this.start();
    }

    @Override
    public IMachineState stop(IMachineState swap) {
        this.stop();
        swap.start(this.moe);
        return swap;
    }

    public void setMoe(MoeEntity moe) {
        this.moe = moe;
    }
}
