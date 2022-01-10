package block_party.npc.state;

import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.state.GoalState;

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
        this.npc.getLookControl().setLookAt(this.npc.getPlayer(), this.npc.getHeadRotSpeed() * this.multiplier, this.npc.getMaxHeadXRot() * this.multiplier);
        return --this.totalTicks < 0;
    }

    @Override
    public void onComplete() {
        this.totalTicks = 0;
    }
}
