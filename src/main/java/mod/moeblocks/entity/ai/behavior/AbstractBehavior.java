package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.AbstractMoeState;
import mod.moeblocks.entity.ai.IMachineState;
import mod.moeblocks.util.MoeBlockAliases;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;

public class AbstractBehavior extends AbstractMoeState {
    @Override
    public void start(StateEntity entity) {
        super.start(entity);
        if (this.moe.isLocal()) {
            float priority = this.moe.isReallyImmuneToFire() ? 0.0F : -1.0F;
            this.moe.setPathPriority(PathNodeType.DAMAGE_FIRE, priority);
            this.moe.setPathPriority(PathNodeType.DANGER_FIRE, priority);
            this.moe.setPathPriority(PathNodeType.LAVA, priority);
            this.moe.setPathPriority(PathNodeType.WATER, priority);
            this.moe.getAttribute(Attributes.ARMOR).applyNonPersistentModifier(this.getArmorModifier());
            this.moe.setScale(this.getBlockVolume());
        }
    }

    public float getBlockVolume() {
        VoxelShape shape = this.getBlockState().getRenderShape(this.moe.world, this.moe.getPosition());
        float dX = (float) (shape.getEnd(Direction.Axis.X) - shape.getStart(Direction.Axis.X));
        float dY = (float) (shape.getEnd(Direction.Axis.Y) - shape.getStart(Direction.Axis.Y));
        float dZ = (float) (shape.getEnd(Direction.Axis.Z) - shape.getStart(Direction.Axis.Z));
        float volume = (float) (Math.cbrt(dX * dY * dZ));
        return Float.isFinite(volume) ? Math.min(Math.max(volume, 0.25F), 1.5F) : 1.0F;
    }

    public AttributeModifier getArmorModifier() {
        return new AttributeModifier(this.moe.getUniqueID(), "Block-based armor modifier", this.getBlockState().getBlockHardness(this.moe.world, this.moe.getPosition()) * 1.8F, AttributeModifier.Operation.ADDITION);
    }

    public BlockState getBlockState() {
        return this.moe.getBlockData();
    }

    @Override
    public IMachineState stop(IMachineState swap) {
        if (this.moe.isLocal() && this.moe != null) {
            this.moe.getAttribute(Attributes.ARMOR).removeModifier(this.moe.getUniqueID());
            this.moe.setCanFly(false);
        }
        return super.stop(swap);
    }

    @Override
    public void start() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {

    }

    @Override
    public void write(CompoundNBT compound) {

    }

    @Override
    public void onDeath(DamageSource cause) {

    }

    @Override
    public void onSpawn(IWorld world) {

    }

    @Override
    public boolean onDamage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        return false;
    }

    @Override
    public boolean isArmed() {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return false;
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

    public SoundEvent getStepSound() {
        return null;
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

    @Override
    public Enum<?> getKey() {
        return null;
    }

    @Override
    public boolean matches(Enum<?>... keys) {
        return false;
    }
}
