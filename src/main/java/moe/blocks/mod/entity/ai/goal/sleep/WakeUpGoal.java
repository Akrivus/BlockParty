package moe.blocks.mod.entity.ai.goal.sleep;

import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class WakeUpGoal extends Goal {
    protected final NPCEntity entity;
    protected final World world;

    public WakeUpGoal(NPCEntity entity) {
        super();
        this.entity = entity;
        this.world = entity.world;
    }

    @Override
    public boolean shouldExecute() {
        if (this.world.isDaytime()) { return true; }
        if (this.world.getEntitiesWithinAABB(MonsterEntity.class, this.entity.getBoundingBox().expand(8.0F, 5.0F, 8.0F)).isEmpty()) {
            Optional<BlockPos> bed = this.entity.getBedPosition();
            if (bed.isPresent()) { return this.world.getBlockState(bed.get()).isBed(this.world, bed.get(), this.entity); }
        }
        return true;
    }

    @Override
    public void startExecuting() {
        this.entity.clearBedPosition();
    }
}
