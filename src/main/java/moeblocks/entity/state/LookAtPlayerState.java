package moeblocks.entity.state;

import moeblocks.automata.IState;
import moeblocks.automata.State;
import moeblocks.automata.state.GoalState;
import moeblocks.entity.AbstractNPCEntity;

public class LookAtPlayerState extends GoalState {
    private int totalTicks = 20;
    private final float multiplier;

    public LookAtPlayerState(float multiplier, int extraTicks) {
        super(3, Flag.LOOK);
        this.totalTicks += extraTicks / multiplier;
        this.multiplier = multiplier;
    }

    @Override
    protected boolean canCompleteOnFirstTry() {
        this.npc.getLookController().setLookPositionWithEntity(this.npc.getPlayer(), this.npc.getFaceRotSpeed() * this.multiplier, this.npc.getVerticalFaceSpeed() * this.multiplier);
        return --this.totalTicks < 0;
    }

    @Override
    protected void onComplete() {
        this.totalTicks = 0;
    }

    @Override
    public IState transfer(AbstractNPCEntity npc) {
        return State.RESET;
    }
}
