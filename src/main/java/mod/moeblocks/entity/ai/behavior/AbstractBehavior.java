package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.ai.IState;
import mod.moeblocks.entity.util.Behaviors;
import mod.moeblocks.util.MoeBlockAliases;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public abstract class AbstractBehavior extends AbstractState {
    public CompoundNBT getExtraBlockData() {
        return this.moe.getExtraBlockData();
    }

    @Override
    public void start(MoeEntity moe) {
        super.start(moe);
        if (!this.moe.world.isRemote()) {
            this.moe.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(this.getArmorModifier());
            this.moe.setScale(this.getBlockVolume());
        }
    }

    public float getBlockVolume() {
        VoxelShape shape = this.getBlockState().getRenderShape(this.moe.world, this.moe.getPosition());
        float dX = (float) (shape.getEnd(Direction.Axis.X) - shape.getStart(Direction.Axis.X));
        float dY = (float) (shape.getEnd(Direction.Axis.Y) - shape.getStart(Direction.Axis.Y));
        float dZ = (float) (shape.getEnd(Direction.Axis.Z) - shape.getStart(Direction.Axis.Z));
        return (float) (Math.cbrt(dX * dY * dZ));
    }

    @Override
    public IState stop(IState swap) {
        if (!this.moe.world.isRemote() && this.moe != null) {
            this.moe.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(this.moe.getUniqueID());
            this.moe.setCanFly(false);
        }
        return super.stop(swap);
    }

    public AttributeModifier getArmorModifier() {
        return new AttributeModifier(this.moe.getUniqueID(), "Block-based armor modifier", this.getBlockState().getBlockHardness(this.moe.world, this.moe.getPosition()) * 1.8F, AttributeModifier.Operation.ADDITION);
    }

    public BlockState getBlockState() {
        return this.moe.getBlockData();
    }

    public Block getBlock() {
        return MoeBlockAliases.get(this.getBlockState().getBlock());
    }

    public boolean isGlowing() {
        return false;
    }

    public float getPitch() {
        float range = (this.moe.world.rand.nextFloat() - this.moe.world.rand.nextFloat()) * 0.2F;
        float mean = Math.max(-0.1F * this.getBlockState().getBlockHardness(this.moe.world, this.moe.getPosition()) / 1.0F * this.moe.getScale() + 1.2F, 0.9F);
        return range * 0.2F + mean + 1.0F - this.moe.getScale();
    }

    public String getFolder() {
        return this.getBlock().getRegistryName().getNamespace();
    }

    public String getFile() {
        return this.getBlock().getRegistryName().getPath();
    }

    public String getPath() {
        return String.format("%s/%s", this.getFolder(), this.getFile());
    }

    @Override
    public String toString() {
        return this.getKey().name();
    }

    public Behaviors getKey() {
        return Behaviors.MISSING;
    }
}
