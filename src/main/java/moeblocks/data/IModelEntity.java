package moeblocks.data;

import moeblocks.data.sql.Row;
import moeblocks.init.MoeWorldData;
import moeblocks.util.DimBlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public interface IModelEntity<M extends Row> {
    World getWorld();
    DimBlockPos getDimBlockPos();
    void setDatabaseID(UUID uuid);
    UUID getDatabaseID();
    void setPlayerUUID(UUID uuid);
    UUID getPlayerUUID();

    boolean hasRow();
    M getRow();
    M getNewRow();

    default boolean claim(PlayerEntity player) {
        if (player.world.isRemote()) { return false; }
        this.setPlayerUUID(player == null ? UUID.fromString("00000000-0000-0000-0000-000000000000") : player.getUniqueID());
        if (!this.hasRow()) {
            this.getNewRow().insert();
        }
        return true;
    }

    default MoeWorldData getData() {
        return MoeWorldData.get(this.getWorld());
    }
}
