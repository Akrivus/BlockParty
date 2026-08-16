package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public record StageAction(Operation operation) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        switch (this.operation) {
            case SIT -> moe.setSitting(true);
            case STAND -> moe.setSitting(false);
            case JUMP -> moe.getJumpControl().jump();
            case SWING_HAND -> moe.swing(InteractionHand.MAIN_HAND);
            case LOOK_AT_ASSIGNMENT -> {
                Vec3 target = moe.sceneDirective().blockTarget().isEmpty()
                        ? moe.sceneDirective().destination(moe)
                        : Vec3.atCenterOf(moe.sceneDirective().blockTarget().getPos());
                if (target != null) moe.getLookControl().setLookAt(target.x, target.y, target.z, 30.0F, 30.0F);
            }
        }
    }

    public enum Operation { SIT, STAND, JUMP, SWING_HAND, LOOK_AT_ASSIGNMENT }
}
