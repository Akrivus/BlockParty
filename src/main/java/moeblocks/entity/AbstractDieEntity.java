package moeblocks.entity;

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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractDieEntity extends ProjectileItemEntity {
    private static final DataParameter<Rotations> ROTATIONS = EntityDataManager.createKey(AbstractDieEntity.class, DataSerializers.ROTATIONS);
    private static final Map<Vector3f, Integer> MAP = new LinkedHashMap<>(64);

    static {
        MAP.put(new Vector3f(0, 0, 0), 2);
        MAP.put(new Vector3f(0, 0, 90), 3);
        MAP.put(new Vector3f(0, 0, 180), 5);
        MAP.put(new Vector3f(0, 0, 270), 4);
        MAP.put(new Vector3f(0, 90, 0), 2);
        MAP.put(new Vector3f(0, 90, 90), 6);
        MAP.put(new Vector3f(0, 90, 180), 5);
        MAP.put(new Vector3f(0, 90, 270), 1);
        MAP.put(new Vector3f(0, 180, 0), 2);
        MAP.put(new Vector3f(0, 180, 90), 4);
        MAP.put(new Vector3f(0, 180, 180), 5);
        MAP.put(new Vector3f(0, 180, 270), 3);
        MAP.put(new Vector3f(0, 270, 0), 2);
        MAP.put(new Vector3f(0, 270, 90), 1);
        MAP.put(new Vector3f(0, 270, 180), 5);
        MAP.put(new Vector3f(0, 270, 270), 6);
        MAP.put(new Vector3f(90, 0, 0), 1);
        MAP.put(new Vector3f(90, 0, 90), 3);
        MAP.put(new Vector3f(90, 0, 180), 6);
        MAP.put(new Vector3f(90, 0, 270), 4);
        MAP.put(new Vector3f(90, 90, 0), 1);
        MAP.put(new Vector3f(90, 90, 90), 2);
        MAP.put(new Vector3f(90, 90, 180), 6);
        MAP.put(new Vector3f(90, 90, 270), 5);
        MAP.put(new Vector3f(90, 180, 0), 1);
        MAP.put(new Vector3f(90, 180, 90), 4);
        MAP.put(new Vector3f(90, 180, 180), 6);
        MAP.put(new Vector3f(90, 180, 270), 3);
        MAP.put(new Vector3f(90, 270, 0), 1);
        MAP.put(new Vector3f(90, 270, 90), 5);
        MAP.put(new Vector3f(90, 270, 180), 6);
        MAP.put(new Vector3f(90, 270, 270), 2);
        MAP.put(new Vector3f(180, 0, 0), 5);
        MAP.put(new Vector3f(180, 0, 90), 3);
        MAP.put(new Vector3f(180, 0, 180), 2);
        MAP.put(new Vector3f(180, 0, 270), 4);
        MAP.put(new Vector3f(180, 90, 0), 5);
        MAP.put(new Vector3f(180, 90, 90), 1);
        MAP.put(new Vector3f(180, 90, 180), 2);
        MAP.put(new Vector3f(180, 90, 270), 6);
        MAP.put(new Vector3f(180, 180, 0), 5);
        MAP.put(new Vector3f(180, 180, 90), 4);
        MAP.put(new Vector3f(180, 180, 180), 2);
        MAP.put(new Vector3f(180, 180, 270), 3);
        MAP.put(new Vector3f(180, 270, 0), 5);
        MAP.put(new Vector3f(180, 270, 90), 6);
        MAP.put(new Vector3f(180, 270, 180), 2);
        MAP.put(new Vector3f(180, 270, 270), 1);
        MAP.put(new Vector3f(270, 0, 0), 6);
        MAP.put(new Vector3f(270, 0, 90), 3);
        MAP.put(new Vector3f(270, 0, 180), 1);
        MAP.put(new Vector3f(270, 0, 270), 4);
        MAP.put(new Vector3f(270, 90, 0), 6);
        MAP.put(new Vector3f(270, 90, 90), 5);
        MAP.put(new Vector3f(270, 90, 180), 1);
        MAP.put(new Vector3f(270, 90, 270), 2);
        MAP.put(new Vector3f(270, 180, 0), 6);
        MAP.put(new Vector3f(270, 180, 90), 4);
        MAP.put(new Vector3f(270, 180, 180), 1);
        MAP.put(new Vector3f(270, 180, 270), 3);
        MAP.put(new Vector3f(270, 270, 0), 6);
        MAP.put(new Vector3f(270, 270, 90), 2);
        MAP.put(new Vector3f(270, 270, 180), 1);
        MAP.put(new Vector3f(270, 270, 270), 5);
    }

    private final int spin = 30;
    private boolean landed;
    private int totalHops;
    private int face = -1;

    public AbstractDieEntity(EntityType<? extends AbstractDieEntity> type, World world) {
        super(type, world);
    }

    public AbstractDieEntity(EntityType<? extends AbstractDieEntity> type, World world, double x, double y, double z) {
        super(type, x, y, z, world);
    }

    public AbstractDieEntity(EntityType<? extends AbstractDieEntity> type, World world, LivingEntity thrower) {
        super(type, thrower, world);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote()) { return; }
        if (--this.totalHops < 0 && result.getType() == RayTraceResult.Type.BLOCK) {
            if (this.world.isAirBlock(this.getPositionUnderneath())) {
                this.bounce();
            } else {
                BlockRayTraceResult block = (BlockRayTraceResult) result;
                BlockPos pos = block.getPos();
                BlockState state = this.world.getBlockState(pos);
                this.landed = true;
                if (this.isLanded() && this.onActionStart(state, pos, this.getRandomFace())) {
                    this.setPositionAndUpdate(this.getPosX(), Math.round(this.getPosY()) - 0.15F, this.getPosZ());
                    this.setNoGravity(true);
                    this.setVelocity(0, 0, 0);
                    this.setMotion(Vector3d.ZERO);
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

    @Override
    protected void registerData() {
        this.dataManager.register(ROTATIONS, this.getRandomSpinRotations());
        super.registerData();
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

    public Rotations getRotations() {
        return this.dataManager.get(ROTATIONS);
    }

    public void setRotations(Rotations rotations) {
        this.dataManager.set(ROTATIONS, rotations);
    }

    private Rotations getRandomSpinRotations() {
        Rotations rotation = this.getRandomFaceRotations();
        float x = rotation.getX() + this.getSpin();
        float y = rotation.getY() + this.getSpin();
        float z = rotation.getZ() + this.getSpin();
        return new Rotations(x, y, z);
    }

    private int getSpin() {
        return this.rand.nextInt(this.spin) * (this.rand.nextInt(3) - 1);
    }

    private Rotations getRandomFaceRotations() {
        this.face = this.rand.nextInt(6) + 1;
        Vector3f face = new Vector3f(0, 0, 0);
        Iterator<Map.Entry<Vector3f, Integer>> it = MAP.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector3f, Integer> entry = it.next();
            if (this.face != entry.getValue()) { continue; }
            face = entry.getKey();
        }
        return new Rotations(face.getX(), face.getY(), face.getZ());
    }

    private int getRandomFace() {
        this.setRotations(this.getRandomFaceRotations());
        return this.face;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote()) { return; }
        double x = this.getMotion().x * this.spin;
        double y = this.getMotion().y * this.spin;
        double z = this.getMotion().z * this.spin;
        this.addRotations(new Vector3d(x, y, z));
        if (this.isLanded()) {
            if (this.onActionTick()) { this.remove(); }
            this.setVelocity(0, 0, 0);
            this.setMotion(Vector3d.ZERO);
            this.setNoGravity(true);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean onActionTick() {
        return false;
    }

    public void addRotations(Vector3d rotation) {
        float x = this.getRotations().getX() + (float) rotation.x;
        float y = this.getRotations().getY() + (float) rotation.y;
        float z = this.getRotations().getZ() + (float) rotation.z;
        this.setRotations(x, y, z);
    }

    public void setRotations(float x, float y, float z) {
        this.setRotations(new Rotations(x, y, z));
    }

    public boolean isLanded() {
        if (this.face < 0) { this.getFaceFromAngle(); }
        return this.face > 0 && this.landed;
    }

    public int getFaceFromAngle() {
        return this.face = AbstractDieEntity.match(this.getPitch(), this.getYaw(), this.getRoll());
    }

    public float getYaw() {
        return this.getRotations().getY() % 360;
    }

    public float getPitch() {
        return this.getRotations().getX() % 360;
    }

    public float getRoll() {
        return this.getRotations().getZ() % 360;
    }

    protected static int match(float x, float y, float z) {
        return MAP.getOrDefault(new Vector3f(round(x), round(y), round(z)), -1);
    }

    private static float round(float angle) {
        if (255 < angle || angle < 285) { return 270; }
        if (165 < angle || angle < 195) { return 180; }
        if (75 < angle || angle < 105) { return 90; }
        if (345 < angle || angle < 15) { return 0; }
        return angle;
    }

    public Vector3d bounce() {
        this.setMotion(this.getMotion().inverse().scale(0.8F));
        return this.getMotion();
    }

    public abstract boolean onActionStart(BlockState state, BlockPos pos, int face);

    public PlayerEntity getPlayer() {
        if (!(this.func_234616_v_() instanceof PlayerEntity)) { return null; }
        return (PlayerEntity) this.func_234616_v_();
    }
}
