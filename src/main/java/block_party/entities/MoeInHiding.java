package block_party.entities;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.data.HidingSpots;
import block_party.registry.CustomEntities;
import block_party.scene.SceneTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MoeInHiding extends Entity {
    public static final EntityDataAccessor<BlockPos> ATTACH_POS = SynchedEntityData.defineId(MoeInHiding.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<String> DATABASE_ID = SynchedEntityData.defineId(MoeInHiding.class, EntityDataSerializers.STRING);

    public MoeInHiding(EntityType<MoeInHiding> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public MoeInHiding(Moe moe) {
        super(CustomEntities.MOE_IN_HIDING.get(), moe.level);
        this.setAttachPos(moe.blockPosition());
        this.setDatabaseID(moe.getDatabaseID());
        HidingSpots.add(moe);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(ATTACH_POS, BlockPos.ZERO);
        this.entityData.define(DATABASE_ID, "-1");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong("AttachPos", this.getAttachPos().asLong());
        compound.putLong("DatabaseID", this.getDatabaseID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setAttachPos(BlockPos.of(compound.getLong("AttachPos")));
        this.setDatabaseID(compound.getLong("DatabaseID"));
    }

    @Override
    public void tick() {
        this.absMoveTo(this.getAttachPos().getX() + 0.5, this.getAttachPos().getY(), this.getAttachPos().getZ() + 0.5);
        super.tick();
        if (this.isRemoved())
            return;
        if (this.level.getBlockState(this.getAttachPos()).isAir())
            this.spawn();
    }

    @Override
    public boolean hurt(DamageSource cause, float amount) {
        if (this.level.getBlockState(this.getAttachPos()).isAir()) {
            this.getRow().update((row) -> row.get(NPC.DEAD).set(true));
            this.kill();
            return true;
        } else {
            this.spawn();
            return false;
        }
    }

    @Override
    public void kill() {
        HidingSpots.remove(this);
        super.kill();
    }

    public boolean spawn() {
        if (this.level.isClientSide()) { return false; }
        BlockPos pos = this.getAttachPos();
        Moe moe = new Moe(this.level);
        moe.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        moe.sceneManager.trigger(SceneTrigger.HIDING_SPOT_DISCOVERED);
        this.getRow().load(moe);
        moe.setBlockState(this.level.getBlockState(pos));
        if (moe.getActualBlockState().hasBlockEntity())
            moe.setTileEntityData(this.level.getBlockEntity(pos).getTileData());
        this.level.destroyBlock(pos, false);
        boolean spawned = this.level.addFreshEntity(moe);
        if (spawned) { this.kill(); }
        return spawned;
    }

    public BlockPos getAttachPos() {
        return this.entityData.get(ATTACH_POS);
    }

    public void setAttachPos(BlockPos pos) {
        this.entityData.set(ATTACH_POS, pos);
    }

    public long getDatabaseID() {
        return Long.parseLong(this.entityData.get(DATABASE_ID));
    }

    public void setDatabaseID(long id) {
        this.entityData.set(DATABASE_ID, Long.toString(id));
    }

    public NPC getRow() {
        return BlockPartyDB.NPCs.find(this.getDatabaseID());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
