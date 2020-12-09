package moeblocks.entity.ai.goal.target;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.sort.EntityDistance;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;

import java.util.List;

public class HostileMobsTarget extends AbstractStateTarget {

    public HostileMobsTarget(AbstractNPCEntity entity) {
        super(entity);
    }

    @Override
    public int getPriority() {
        return 0x3;
    }

    @Override
    public boolean setTarget() {
        if (this.entity.ticksExisted % 20 != 0) { return false; }
        List<MobEntity> victims = this.entity.world.getLoadedEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow(16.0F, 2.0F, 16.0F), victim -> victim instanceof IMob && this.entity.canAttack(victim));
        victims.sort(new EntityDistance(this.entity));
        this.target = victims.isEmpty() ? null : victims.get(0);
        return this.target != null;
    }
}
