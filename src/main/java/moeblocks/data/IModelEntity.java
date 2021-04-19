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
        if (player.world.isRemote()) { return; }
        if (player == null) {
            this.setPlayerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        } else {
            MoeData.get(player.world).addTo(player, this.getDatabaseID());
            this.setPlayerUUID(player.getUniqueID());
        }
        if (!this.hasRow()) {
            this.getNewRow().insert();
        }
    }

    default MoeData getData() {
        return MoeData.get(this.getWorld());
    }
}
