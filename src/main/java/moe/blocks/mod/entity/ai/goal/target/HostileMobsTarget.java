package moe.blocks.mod.entity.ai.goal.target;

import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.util.sort.EntityDistance;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;

import java.util.List;
import java.util.stream.Collectors;

public class HostileMobsTarget extends AbstractStateTarget {

    public HostileMobsTarget(NPCEntity entity) {
        super(entity);
    }

    @Override
    public boolean setTarget() {
        List<MobEntity> victims = this.entity.world.getLoadedEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow(8.0F, 2.0F, 8.0F), victim -> victim instanceof IMob && this.entity.canAttack(victim));
        victims.sort(new EntityDistance(this.entity));
        this.target = victims.isEmpty() ? null : victims.get(0);
        return this.target != null;
    }

    @Override
    public int getPriority() {
        return 0x3;
    }
}
