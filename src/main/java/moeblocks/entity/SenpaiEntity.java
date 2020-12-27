package moeblocks.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class SenpaiEntity extends AbstractNPCEntity {
    public SenpaiEntity(EntityType<? extends SenpaiEntity> type, World world) {
        super(type, world);
    }
    
    @Override
    protected void onTeleport() {
    
    }
    
    @Override
    public int getBaseAge() {
        return 18;
    }
}
