package moeblocks.entity;

import moeblocks.automata.trait.Dere;
import moeblocks.init.MoeEntities;
import moeblocks.init.MoeItems;
import moeblocks.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoeDieEntity extends AbstractDieEntity {
    private BlockState blockStateForSpawn;
    private Dere dere;
    private int timeUntilSpawned;

    public MoeDieEntity(EntityType<MoeDieEntity> type, World world) {
        super(type, world);
    }

    public MoeDieEntity(World world, double x, double y, double z) {
        super(MoeEntities.MOE_DIE.get(), world, x, y, z);
    }

    public MoeDieEntity(World world, LivingEntity thrower) {
        super(MoeEntities.MOE_DIE.get(), world, thrower);
    }

    @Override
    public boolean onActionTick() {
        if (--this.timeUntilSpawned > 0) {
            return false;
        }
        if (this.world.getBlockState(this.getPositionUnderneath()).equals(this.blockStateForSpawn)) {
            return MoeEntity.spawn(this.world, this.getPositionUnderneath(), this.getPosition(), this.rotationYaw, this.rotationPitch, this.dere, this.getPlayer());
        }
        this.entityDropItem(this.getDefaultItem());
        return true;
    }

    @Override
    protected Item getDefaultItem() {
        return MoeItems.MOE_DIE.get();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putInt("TimeUntilSpawned", this.timeUntilSpawned);
        super.writeAdditional(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.timeUntilSpawned = compound.getInt("TimeUntilSpawned");
        super.readAdditional(compound);
    }

    @Override
    public boolean onActionStart(BlockState state, BlockPos pos, int face) {
        if (!state.getBlock().isIn(MoeTags.Blocks.MOEABLES)) {
            return false;
        }
        this.blockStateForSpawn = state;
        this.dere = Dere.values()[face];
        this.timeUntilSpawned = 30;
        return true;
    }
}
