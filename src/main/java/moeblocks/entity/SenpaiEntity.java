package moeblocks.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
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
