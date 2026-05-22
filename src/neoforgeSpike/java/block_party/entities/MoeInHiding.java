package block_party.entities;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import block_party.registry.CustomEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.SQLException;
import java.util.UUID;

public class MoeInHiding extends Entity {
    private static final String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";

    public static final EntityDataAccessor<String> DATABASE_ID =
            SynchedEntityData.defineId(MoeInHiding.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<BlockPos> ATTACH_POS =
            SynchedEntityData.defineId(MoeInHiding.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<String> OWNER_UUID =
            SynchedEntityData.defineId(MoeInHiding.class, EntityDataSerializers.STRING);

    private int ticksHidden;
    private HideUntil hideUntil = HideUntil.EXPOSED;

    public MoeInHiding(EntityType<? extends MoeInHiding> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATABASE_ID, "-1");
        builder.define(ATTACH_POS, BlockPos.ZERO);
        builder.define(OWNER_UUID, EMPTY_UUID);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setDatabaseID(compound.getLong("DatabaseID"));
        this.setAttachPos(BlockPos.of(compound.getLong("AttachPos")));
        this.setHideUntil(HideUntil.fromValue(compound.getString("HideUntil")));
        this.setTicksHidden(compound.getInt("TicksHidden"));
        if (compound.contains("OwnerUUID")) {
            this.setOwnerUUID(UUID.fromString(compound.getString("OwnerUUID")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong("DatabaseID", this.getDatabaseID());
        compound.putLong("AttachPos", this.getAttachPos().asLong());
        compound.putString("HideUntil", this.getHideUntil().getValue());
        compound.putInt("TicksHidden", this.getTicksHidden());
        compound.putString("OwnerUUID", this.getOwnerUUID().toString());
    }

    @Override
    public void tick() {
        BlockPos pos = this.getAttachPos();
        this.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        ++this.ticksHidden;
        super.tick();
        if (!this.isRemoved() && this.getHideUntil().isOver(this)) {
            this.reveal();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (this.isAir()) {
            BlockPartyDB db = BlockPartyDB.get(level);
            try {
                db.findNpc(this.getDatabaseID()).ifPresent(row -> {
                    try {
                        row.markDead(db);
                    } catch (SQLException ignored) {
                    }
                });
            } catch (SQLException ignored) {
            }
            HidingSpots.get(level).remove(this.getAttachPos());
            this.discard();
            return true;
        }
        return this.reveal() != null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
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

    public int getTicksHidden() {
        return this.ticksHidden;
    }

    public void setTicksHidden(int ticksHidden) {
        this.ticksHidden = ticksHidden;
    }

    public UUID getOwnerUUID() {
        return UUID.fromString(this.entityData.get(OWNER_UUID));
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.entityData.set(OWNER_UUID, ownerUUID.toString());
    }

    public Moe reveal() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        BlockPos pos = this.getAttachPos();
        BlockState state = serverLevel.getBlockState(pos);
        if (state.isAir()) {
            return null;
        }

        BlockPartyDB db = BlockPartyDB.get(serverLevel);
        NPC row;
        try {
            row = db.findNpc(this.getDatabaseID()).orElse(null);
            if (row == null) {
                return null;
            }
        } catch (SQLException exception) {
            return null;
        }

        Moe moe = new Moe(CustomEntities.MOE.get(), serverLevel);
        row.applyTo(moe);
        moe.setBlockState(state);
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (blockEntity != null) {
            moe.setTileEntityData(blockEntity.getPersistentData());
        }
        moe.moveToBlock(pos);
        if (!serverLevel.addFreshEntity(moe)) {
            return null;
        }

        try {
            row.markRevealed(db, serverLevel, pos);
        } catch (SQLException exception) {
            moe.discard();
            return null;
        }
        serverLevel.destroyBlock(pos, false);
        HidingSpots.get(serverLevel).remove(pos);
        this.discard();
        return moe;
    }

    public boolean isAir() {
        return this.level().getBlockState(this.getAttachPos()).isAir();
    }
}
