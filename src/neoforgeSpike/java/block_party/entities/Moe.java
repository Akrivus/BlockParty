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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.SQLException;
import java.util.UUID;

public class Moe extends Entity {
    private static final String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";

    public static final EntityDataAccessor<String> DATABASE_ID =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> OWNER_UUID =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<BlockState> BLOCK_STATE =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Boolean> FOLLOWING =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> GIVEN_NAME =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> GENDER =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);

    private CompoundTag tileEntityData = new CompoundTag();

    public Moe(EntityType<? extends Moe> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATABASE_ID, "-1");
        builder.define(OWNER_UUID, EMPTY_UUID);
        builder.define(BLOCK_STATE, Blocks.AIR.defaultBlockState());
        builder.define(FOLLOWING, false);
        builder.define(GIVEN_NAME, "Tokumei");
        builder.define(GENDER, "female");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setDatabaseID(compound.getLong("DatabaseID"));
        this.setBlockState(Block.stateById(compound.getInt("BlockState")));
        this.setFollowing(compound.getBoolean("Following"));
        if (compound.contains("OwnerUUID")) {
            this.setOwnerUUID(UUID.fromString(compound.getString("OwnerUUID")));
        }
        if (compound.contains("GivenName")) {
            this.setGivenName(compound.getString("GivenName"));
        }
        if (compound.contains("Gender")) {
            this.setGender(compound.getString("Gender"));
        }
        this.setTileEntityData(compound.getCompound("TileEntity"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong("DatabaseID", this.getDatabaseID());
        compound.putInt("BlockState", Block.getId(this.getBlockState()));
        compound.putBoolean("Following", this.isFollowing());
        compound.putString("OwnerUUID", this.getOwnerUUID().toString());
        compound.putString("GivenName", this.getGivenName());
        compound.putString("Gender", this.getGender());
        compound.put("TileEntity", this.getTileEntityData().copy());
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    public long getDatabaseID() {
        return Long.parseLong(this.entityData.get(DATABASE_ID));
    }

    public void setDatabaseID(long id) {
        this.entityData.set(DATABASE_ID, Long.toString(id));
    }

    public UUID getOwnerUUID() {
        return UUID.fromString(this.entityData.get(OWNER_UUID));
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.entityData.set(OWNER_UUID, ownerUUID.toString());
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, state);
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public void setFollowing(boolean following) {
        this.entityData.set(FOLLOWING, following);
    }

    public String getGivenName() {
        return this.entityData.get(GIVEN_NAME);
    }

    public void setGivenName(String givenName) {
        this.entityData.set(GIVEN_NAME, givenName);
    }

    public String getGender() {
        return this.entityData.get(GENDER);
    }

    public void setGender(String gender) {
        this.entityData.set(GENDER, gender);
    }

    public CompoundTag getTileEntityData() {
        return this.tileEntityData.copy();
    }

    public void setTileEntityData(CompoundTag tileEntityData) {
        this.tileEntityData = tileEntityData == null ? new CompoundTag() : tileEntityData.copy();
    }

    public void moveToBlock(BlockPos pos) {
        this.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public MoeInHiding hide(HideUntil until) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
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

        BlockPos pos = this.blockPosition();
        serverLevel.setBlock(pos, this.getBlockState(), 3);
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.getPersistentData().merge(this.getTileEntityData());
            blockEntity.setChanged();
        }

        MoeInHiding hiding = new MoeInHiding(CustomEntities.MOE_IN_HIDING.get(), serverLevel);
        hiding.setDatabaseID(this.getDatabaseID());
        hiding.setOwnerUUID(this.getOwnerUUID());
        hiding.setAttachPos(pos);
        hiding.setHideUntil(until);
        hiding.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        if (!serverLevel.addFreshEntity(hiding)) {
            return null;
        }
        try {
            row.updateFromMoe(db, serverLevel, this);
            row.markHiding(db, serverLevel, pos, this.getBlockState());
        } catch (SQLException exception) {
            hiding.discard();
            return null;
        }
        HidingSpots.get(serverLevel).put(pos, this.getDatabaseID());
        this.discard();
        return hiding;
    }
}
