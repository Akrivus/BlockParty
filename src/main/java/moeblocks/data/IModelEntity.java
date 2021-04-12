package moeblocks.data;

import moeblocks.data.sql.Row;
import moeblocks.init.MoeData;
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

    default void claim(PlayerEntity player) {
        this.setPlayerUUID(player.getUniqueID());
        if (!this.hasRow()) {
            this.getNewRow().insert();
        }
    }

    default MoeData getData() {
        return MoeData.get(this.getWorld());
    }
}
