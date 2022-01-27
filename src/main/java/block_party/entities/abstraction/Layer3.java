package block_party.entities.abstraction;

import block_party.entities.BlockPartyNPC;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Abstraction layer 3: player ownership.
 */
public abstract class Layer3 extends Layer2 {
    public static final EntityDataAccessor<String> PLAYER_UUID = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> FOLLOWING = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.BOOLEAN);

    protected Layer3(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(PLAYER_UUID, "00000000-0000-0000-0000-000000000000");
        this.entityData.define(FOLLOWING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Following", this.isFollowing());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setFollowing(compound.getBoolean("Following"));
        super.readAdditionalSaveData(compound);
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public void setFollowing(boolean following) {
        this.entityData.set(FOLLOWING, following);
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(this.entityData.get(PLAYER_UUID));
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.entityData.set(PLAYER_UUID, playerUUID.toString());
    }

    public Player getPlayer() {
        return this.level.getPlayerByUUID(this.getPlayerUUID());
    }

    public ServerPlayer getServerPlayer() {
        return this.getServer().getPlayerList().getPlayer(this.getPlayerUUID());
    }

    public void setPlayer(Player player) {
        this.setPlayerUUID(player.getUUID());
    }

    public boolean isPlayerOnline() {
        return this.getPlayer() != null;
    }

    public boolean isPlayerBusy() {
        return this.isPlayerOnline() && this.getPlayer().containerMenu != this.getPlayer().inventoryMenu;
    }

    public boolean isPlayer(Entity entity) {
        return entity != null && this.getPlayerUUID().equals(entity.getUUID());
    }

    public boolean isBeingLookedAt() {
        if (!this.isPlayerOnline()) { return false; }
        Player player = this.getPlayer();
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 dist = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
        double d0 = dist.length();
        dist = dist.normalize();
        double d1 = look.dot(dist);
        return d1 > 1.0D - 0.025D / d0 ? player.hasLineOfSight(this) : false;
    }
}
