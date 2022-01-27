package block_party.scene.actions.goalbased;

import block_party.scene.actions.GoalBasedAction;

public class LookAtPlayerAction extends GoalBasedAction {
    private final float multiplier;
    private int totalTicks = 20;

    public LookAtPlayerAction(float multiplier, int extraTicks) {
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
