package moeblocks.entity;

import moeblocks.init.MoeEntities;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DeerEntity extends AnimalEntity {
    public DeerEntity(EntityType<? extends DeerEntity> type, World world) {
        super(type, world);
    }

    @Override
    public DeerEntity func_241840_a(ServerWorld world, AgeableEntity parent) {
        return MoeEntities.DEER.get().create(world);
    }
}
