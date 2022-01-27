package block_party.entities.abstraction;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.Recordable;
import block_party.db.records.NPC;
import block_party.entities.BlockPartyNPC;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstraction layer 5: database row and sync.
 */
public abstract class Layer5 extends Layer4 implements Recordable<NPC> {
    public static final EntityDataAccessor<String> DATABASE_ID = SynchedEntityData.defineId(Layer5.class, EntityDataSerializers.STRING);
    private boolean syncWithDB;
    private boolean readyToSync;

    public Layer5(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(DATABASE_ID, "-1");
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong("DatabaseID", this.getDatabaseID());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setDatabaseID(compound.getLong("DatabaseID"));
        super.readAdditionalSaveData(compound);
        if (this.isSyncWithDB()) {
            this.getRow().load(this.cast());
            this.readyToSync = true;
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (this.hasRow()) { this.getRow().update(this.cast()); }
    }
    @Override
    public NPC getRow() {
        return BlockPartyDB.NPCs.find(this.getDatabaseID());
    }

    @Override
    public boolean hasRow() {
        return this.isLocal() && this.readyToSync && this.getRow() != null;
    }

    @Override
    public NPC getNewRow() {
        return new NPC(this.cast());
    }

    @Override
    public boolean claim(Player player) {
        if (this.syncWithDB && Recordable.super.claim(player)) {
            this.getData().addTo(player, this.getDatabaseID());
            this.readyToSync = true;
        }
        return this.isLocal();
    }

    @Override
    public Level getWorld() {
        return this.level;
    }

    @Override
    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.level.dimension(), this.blockPosition());
    }

    public long getDatabaseID() {
        return Long.parseLong(this.entityData.get(DATABASE_ID));
    }

    public void setDatabaseID(long id) {
        this.entityData.set(DATABASE_ID, Long.toString(id));
    }

    public void doSyncWithDatabase(boolean syncWithDB) {
        this.syncWithDB = syncWithDB;
    }

    public boolean isSyncWithDB() {
        return this.syncWithDB;
    }
}
