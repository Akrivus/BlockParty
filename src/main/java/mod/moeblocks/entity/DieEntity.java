package mod.moeblocks.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.HashMap;

public abstract class DieEntity extends ProjectileItemEntity {
    private static final DataParameter<Rotations> ROTATIONS = EntityDataManager.createKey(DieEntity.class, DataSerializers.ROTATIONS);
    private static final HashMap<Vec3i, Face> DIE_FACE = new HashMap<>();

    static {
        DIE_FACE.put(new Vec3i(0, 0, 0), Face.FIVE);
        DIE_FACE.put(new Vec3i(0, 90, 0), Face.FIVE);
        DIE_FACE.put(new Vec3i(0, 180, 0), Face.FIVE);
        DIE_FACE.put(new Vec3i(0, 270, 0), Face.FIVE);
        DIE_FACE.put(new Vec3i(90, 0, 0), Face.ONE);
        DIE_FACE.put(new Vec3i(90, 90, 0), Face.ONE);
        DIE_FACE.put(new Vec3i(90, 180, 0), Face.ONE);
        DIE_FACE.put(new Vec3i(90, 270, 0), Face.ONE);
        DIE_FACE.put(new Vec3i(180, 0, 0), Face.TWO);
        DIE_FACE.put(new Vec3i(180, 90, 0), Face.TWO);
        DIE_FACE.put(new Vec3i(180, 180, 0), Face.TWO);
        DIE_FACE.put(new Vec3i(180, 270, 0), Face.TWO);
        DIE_FACE.put(new Vec3i(270, 0, 0), Face.SIX);
        DIE_FACE.put(new Vec3i(270, 90, 0), Face.SIX);
        DIE_FACE.put(new Vec3i(270, 180, 0), Face.SIX);
        DIE_FACE.put(new Vec3i(270, 270, 0), Face.SIX);
        DIE_FACE.put(new Vec3i(0, 0, 90), Face.FOUR);
        DIE_FACE.put(new Vec3i(0, 90, 90), Face.SIX);
        DIE_FACE.put(new Vec3i(0, 180, 90), Face.THREE);
        DIE_FACE.put(new Vec3i(0, 270, 90), Face.ONE);
        DIE_FACE.put(new Vec3i(90, 0, 90), Face.FOUR);
        DIE_FACE.put(new Vec3i(90, 90, 90), Face.FIVE);
        DIE_FACE.put(new Vec3i(90, 180, 90), Face.THREE);
        DIE_FACE.put(new Vec3i(90, 270, 90), Face.TWO);
        DIE_FACE.put(new Vec3i(180, 0, 90), Face.FOUR);
        DIE_FACE.put(new Vec3i(180, 90, 90), Face.ONE);
        DIE_FACE.put(new Vec3i(180, 180, 90), Face.THREE);
        DIE_FACE.put(new Vec3i(180, 270, 90), Face.SIX);
        DIE_FACE.put(new Vec3i(270, 0, 90), Face.FOUR);
        DIE_FACE.put(new Vec3i(270, 90, 90), Face.TWO);
        DIE_FACE.put(new Vec3i(270, 180, 90), Face.THREE);
        DIE_FACE.put(new Vec3i(270, 270, 90), Face.FIVE);
        DIE_FACE.put(new Vec3i(0, 0, 180), Face.TWO);
        DIE_FACE.put(new Vec3i(0, 90, 180), Face.TWO);
        DIE_FACE.put(new Vec3i(0, 180, 180), Face.TWO);
        DIE_FACE.put(new Vec3i(0, 270, 180), Face.TWO);
        DIE_FACE.put(new Vec3i(90, 0, 180), Face.SIX);
        DIE_FACE.put(new Vec3i(90, 90, 180), Face.SIX);
        DIE_FACE.put(new Vec3i(90, 180, 180), Face.SIX);
        DIE_FACE.put(new Vec3i(90, 270, 180), Face.SIX);
        DIE_FACE.put(new Vec3i(180, 0, 180), Face.FIVE);
        DIE_FACE.put(new Vec3i(180, 90, 180), Face.FIVE);
        DIE_FACE.put(new Vec3i(180, 180, 180), Face.FIVE);
        DIE_FACE.put(new Vec3i(180, 270, 180), Face.FIVE);
        DIE_FACE.put(new Vec3i(270, 0, 180), Face.ONE);
        DIE_FACE.put(new Vec3i(270, 90, 180), Face.ONE);
        DIE_FACE.put(new Vec3i(270, 180, 180), Face.ONE);
        DIE_FACE.put(new Vec3i(270, 270, 180), Face.ONE);
        DIE_FACE.put(new Vec3i(0, 0, 270), Face.THREE);
        DIE_FACE.put(new Vec3i(0, 90, 270), Face.ONE);
        DIE_FACE.put(new Vec3i(0, 180, 270), Face.FOUR);
        DIE_FACE.put(new Vec3i(0, 270, 270), Face.SIX);
        DIE_FACE.put(new Vec3i(90, 0, 270), Face.THREE);
        DIE_FACE.put(new Vec3i(90, 90, 270), Face.TWO);
        DIE_FACE.put(new Vec3i(90, 180, 270), Face.FOUR);
        DIE_FACE.put(new Vec3i(90, 270, 270), Face.FIVE);
        DIE_FACE.put(new Vec3i(180, 0, 270), Face.THREE);
        DIE_FACE.put(new Vec3i(180, 90, 270), Face.SIX);
        DIE_FACE.put(new Vec3i(180, 180, 270), Face.FOUR);
        DIE_FACE.put(new Vec3i(180, 270, 270), Face.ONE);
        DIE_FACE.put(new Vec3i(270, 0, 270), Face.THREE);
        DIE_FACE.put(new Vec3i(270, 90, 270), Face.FIVE);
        DIE_FACE.put(new Vec3i(270, 180, 270), Face.FOUR);
        DIE_FACE.put(new Vec3i(270, 270, 270), Face.TWO);
    }

    protected int totalHops;
    private boolean landed;
    private Vec3i rotation;

    public DieEntity(EntityType<? extends DieEntity> type, World world) {
        super(type, world);
    }

    public DieEntity(EntityType<? extends DieEntity> type, World world, double x, double y, double z) {
        super(type, x, y, z, world);
    }

    public DieEntity(EntityType<? extends DieEntity> type, World world, LivingEntity thrower) {
        super(type, thrower, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote()) {
            if (this.landed) {
                if (this.onActionTick()) {
                    this.setVelocity(0, 0, 0);
                    this.setMotion(Vec3d.ZERO);
                    this.setNoGravity(true);
                } else {
                    this.remove();
                }
            } else {
                double x = Math.abs(this.getMotion().x) * (this.rand.nextFloat() * 90.0 + 90.0);
                double y = Math.abs(this.getMotion().y) * (this.rand.nextFloat() * 90.0 + 90.0);
                double z = Math.abs(this.getMotion().z) * (this.rand.nextFloat() * 90.0 + 90.0);
                this.addRotations(new Vec3d(x, y, z));
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote()) {
            --this.totalHops;
            if (result.getType() == RayTraceResult.Type.BLOCK && this.totalHops < 0) {
                if (this.world.isAirBlock(this.getPositionUnderneath())) {
                    this.bounce();
                } else {
                    BlockRayTraceResult block = (BlockRayTraceResult) result;
                    BlockPos pos = block.getPos();
                    BlockState state = this.world.getBlockState(pos);
                    if (this.isLanded() && this.onActionStart(state, pos, this.getFaceFromAngle())) {
                        this.setRotations(this.rotation.getX(), this.rotation.getY(), this.rotation.getZ());
                        this.setVelocity(0, 0, 0);
                        this.setMotion(Vec3d.ZERO);
                        this.setNoGravity(true);
                    } else {
                        this.landed = false;
                        this.bounce();
                    }
                }
            } else if (result.getType() == RayTraceResult.Type.ENTITY) {
                EntityRayTraceResult trace = (EntityRayTraceResult) result;
                Entity entity = trace.getEntity();
                this.setMotion(this.bounce().mul(entity.getMotion()));
            } else {
                this.bounce();
            }
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean onActionTick() {
        return false;
    }

    public void addRotations(Vec3d rotation) {
        float x = this.getRotations().getX() + (float) rotation.x;
        float y = this.getRotations().getY() + (float) rotation.y;
        float z = this.getRotations().getZ() + (float) rotation.z;
        this.setRotations(x, y, z);
    }

    public Rotations getRotations() {
        return this.dataManager.get(ROTATIONS);
    }

    public void setRotations(Rotations rotations) {
        this.dataManager.set(ROTATIONS, rotations);
    }

    public void setRotations(float x, float y, float z) {
        this.setRotations(new Rotations(x, y, z));
    }

    public boolean onActionStart(BlockState state, BlockPos pos, Face face) {
        return false;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ROTATIONS, new Rotations(this.rand.nextFloat() * 360.0F, this.rand.nextFloat() * 360.0F, this.rand.nextFloat() * 360.0F));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Rotations", this.getRotations().writeToNBT());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setRotations(new Rotations(compound.getList("Rotations", 5)));
    }

    public Vec3d bounce() {
        Vec3d motion = this.getMotion().inverse().scale(0.8F);
        this.setMotion(motion);
        return motion;
    }

    public boolean isLanded() {
        return this.landed || (this.landed = this.getFaceFromAngle() != null);
    }

    public Face getFaceFromAngle() {
        int x = 90 * (Math.round(this.getRotations().getX() / 90));
        int y = 90 * (Math.round(this.getRotations().getY() / 90));
        int z = 90 * (Math.round(this.getRotations().getZ() / 90));
        this.rotation = new Vec3i(x, y, z);
        return DIE_FACE.get(this.rotation);
    }

    public PlayerEntity getPlayer() {
        if (this.getThrower() instanceof PlayerEntity) {
            return (PlayerEntity) this.getThrower();
        }
        return null;
    }

    public enum Face {
        ONE, TWO, THREE, FOUR, FIVE, SIX;

        public int getNumber() {
            return this.ordinal() + 1;
        }
    }
}
