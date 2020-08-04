package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RevengeGoal extends TargetGoal {
    protected final MoeEntity moe;
    protected LivingEntity victim;

    public RevengeGoal(MoeEntity moe) {
        super(moe, true);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.moe = moe;
    }

    @Override
    public void startExecuting() {
        if (this.moe.getDere().isArmed()) {
            this.moe.setAttackTarget(this.victim);
        } else {
            List<MoeEntity> moes = this.moe.world.getEntitiesWithinAABB(MoeEntity.class, this.moe.getBoundingBox().grow(8.0F, 4.0F, 8.0F)).stream().filter(moe -> moe.getDere().isArmed()).collect(Collectors.toList());
            if (moes.isEmpty()) {
                this.moe.setAvoidTarget(this.victim);
            } else {
                for (MoeEntity moe : moes) {
                    moe.setAttackTarget(this.victim);
                }
            }
        }
    }
}