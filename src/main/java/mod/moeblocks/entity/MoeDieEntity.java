package mod.moeblocks.entity;

import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.init.MoeEntities;
import mod.moeblocks.init.MoeItems;
import mod.moeblocks.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class MoeDieEntity extends DieEntity {
    protected BlockState blockStateForSpawn;
    protected Deres dere;
    protected int timeUntilSpawned;

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
        if (--this.timeUntilSpawned < 0) {
            BlockState state = this.world.getBlockState(this.getPositionUnderneath());
            TileEntity extra = this.world.getTileEntity(this.getPositionUnderneath());
            if (state.equals(this.blockStateForSpawn)) {
                MoeEntity moe = MoeEntities.MOE.get().create(this.world);
                moe.setPositionAndRotation(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
                moe.setBlockData(state);
                moe.setExtraBlockData(extra != null ? extra.getTileData() : new CompoundNBT());
                moe.setDere(this.dere);
                moe.getRelationships().get(this.getPlayer() != null ? this.getPlayer().getUniqueID() : UUID.randomUUID()).addTrust(100);
                if (this.world.addEntity(moe)) {
                    moe.onInitialSpawn((ServerWorld) this.world, this.world.getDifficultyForLocation(this.getPosition()), SpawnReason.TRIGGERED, null, null);
                    this.world.setBlockState(this.getPositionUnderneath(), Blocks.AIR.getDefaultState());
                    return false;
                }
            } else {
                this.entityDropItem(this.getDefaultItem());
                return false;
            }
        }
        return true;
    }

    @Override
    protected Item getDefaultItem() {
        return MoeItems.MOE_DIE.get();
    }

    @Override
    public boolean onActionStart(BlockState state, BlockPos pos, Face face) {
        if (state.getBlock().isIn(MoeTags.MOEABLES)) {
            this.blockStateForSpawn = state;
            this.dere = Deres.from(face);
            this.timeUntilSpawned = 30;
            return true;
        }
        return false;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("TimeUntilSpawned", this.timeUntilSpawned);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.timeUntilSpawned = compound.getInt("TimeUntilSpawned");
    }
}
