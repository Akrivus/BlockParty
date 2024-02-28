package block_party.entities;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import block_party.registry.CustomEntities;
import block_party.scene.SceneTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MoeInHiding extends Entity {
    public static final EntityDataAccessor<String> DATABASE_ID = SynchedEntityData.defineId(MoeInHiding.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<BlockPos> ATTACH_POS = SynchedEntityData.defineId(MoeInHiding.class, EntityDataSerializers.BLOCK_POS);
    private int ticksHidden;
    private HideUntil hideUntil = HideUntil.EXPOSED;

    public MoeInHiding(EntityType<MoeInHiding> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public MoeInHiding(Moe moe) {
        super(CustomEntities.MOE_IN_HIDING.get(), moe.level);
        this.setDatabaseID(moe.getDatabaseID());
        HidingSpots.add(moe);
        BlockPos pos = moe.blockPosition();
        this.setAttachPos(pos);
        this.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(DATABASE_ID, "-1");
        this.entityData.define(ATTACH_POS, BlockPos.ZERO);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong("DatabaseID", this.getDatabaseID());
        compound.putLong("AttachPos", this.getAttachPos().asLong());
        compound.putString("HideUntil", this.getHideUntil().getValue());
        compound.putInt("TicksHidden", this.ticksHidden);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setDatabaseID(compound.getLong("DatabaseID"));
        this.setAttachPos(BlockPos.of(compound.getLong("AttachPos")));
        this.setHideUntil(HideUntil.EXPOSED.fromValue("HideUntil"));
        this.ticksHidden = compound.getInt("TicksHidden");
    }

    @Override
    public void tick() {
        this.absMoveTo(this.getAttachPos().getX() + 0.5, this.getAttachPos().getY(), this.getAttachPos().getZ() + 0.5);
        super.tick();
        if (this.isRemoved())
            return;
        ++this.ticksHidden;
        if (this.getHideUntil().isOver(this))
            this.spawn();
    }

    @Override
    public boolean hurt(DamageSource cause, float amount) {
        if (this.isAir()) {
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
            moe.setTileEntityData(this.level.getBlockEntity(pos).getPersistentData());
        this.level.destroyBlock(pos, false);
        boolean spawned = this.level.addFreshEntity(moe);
        if (spawned) { this.kill(); }
        return spawned;
    }

    public long getDatabaseID() {
        return Long.parseLong(this.entityData.get(DATABASE_ID));
    }

    public void setDatabaseID(long id) {
        this.entityData.set(DATABASE_ID, Long.toString(id));
    }

    public BlockPos getAttachPos() {
        return this.entityData.get(ATTACH_POS);
    }

    public void setAttachPos(BlockPos pos) {
        this.entityData.set(ATTACH_POS, pos);
    }

    public HideUntil getHideUntil() {
        return this.hideUntil;
    }

    public void setHideUntil(HideUntil hideUntil) {
        this.hideUntil = hideUntil;
    }

    public NPC getRow() {
        return BlockPartyDB.NPCs.find(this.getDatabaseID());
    }

    public int getTicksHidden() {
         return this.ticksHidden;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public boolean isAir() {
        return this.level.getBlockState(this.getAttachPos()).isAir();
    }

}
