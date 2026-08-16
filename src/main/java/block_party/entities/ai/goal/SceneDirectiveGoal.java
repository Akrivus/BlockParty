package block_party.entities.ai.goal;

import block_party.entities.Moe;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

final class SceneDirectiveGoal extends Goal {
    private final Moe moe;

    SceneDirectiveGoal(Moe moe) {
        this.moe = moe;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override public boolean canUse() { return this.moe.sceneDirective().canMove(this.moe); }
    @Override public boolean canContinueToUse() { return this.canUse(); }
    @Override public boolean requiresUpdateEveryTick() { return true; }
    @Override public void tick() { this.moe.sceneDirective().updateMovement(this.moe); }
}
