package block_party.npc.state;

import block_party.scene.actions.GoalBasedAction;

public class LookAtPlayerState extends GoalBasedAction {
    private final float multiplier;
    private int totalTicks = 20;

    public LookAtPlayerState(float multiplier, int extraTicks) {
        super(3, Flag.LOOK);
        this.totalTicks += extraTicks / multiplier;
        this.multiplier = multiplier;
    }

    @Override
    protected boolean canCompleteOnFirstTry() {
        this.npc.ifPlayer(player -> {
            this.npc.getLookControl().setLookAt(player, this.npc.getHeadRotSpeed() * this.multiplier, this.npc.getMaxHeadXRot() * this.multiplier);
        });
        return --this.totalTicks < 0;
    }

    @Override
    public void onComplete() {
        this.totalTicks = 0;
    }
}
