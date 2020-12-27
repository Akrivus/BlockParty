package moeblocks.entity.ai;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.Teleporter;

public class FollowTargetGoal extends AbstractFollowEntityGoal<AbstractNPCEntity, LivingEntity> {
    
    public FollowTargetGoal(AbstractNPCEntity entity) {
        super(entity, LivingEntity.class, 1.1D);
    }
    
    @Override
    public int getPriority() {
        return 0x7;
    }
    
    @Override
    public void resetTask() {
        this.entity.setSprinting(false);
        this.entity.setPathPriority(PathNodeType.WATER, -1.0F);
        super.resetTask();
    }
    
    @Override
    public void onFollow() {
        this.entity.setSprinting(this.entity.getDistance(this.target) > this.getSafeZone(this.target));
    }
    
    @Override
    public void onArrival() {
        this.entity.setSprinting(false);
        if (this.target.isPassenger()) {
            this.entity.startRiding(this.target.getRidingEntity());
        }
    }
    
    @Override
    public float getStrikeZone(LivingEntity target) {
        return 2.0F;
    }
    
    @Override
    public LivingEntity getTarget() {
        return this.entity.getProtagonist();
    }
    
    @Override
    public boolean canFollow(LivingEntity target) {
        return this.entity.isFollowing();
    }
    
    @Override
    public float getSafeZone(LivingEntity target) {
        return 4.0F;
    }
}