package block_party.db;

import block_party.db.sql.Row;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface Recordable<M extends Row> {
    UUID BLANK_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    Level getWorld();
    DimBlockPos getDimBlockPos();
    void setDatabaseID(long id);
    long getDatabaseID();
    void setPlayerUUID(UUID uuid);
    UUID getPlayerUUID();

    boolean hasRow();
    M getRow();
    M getNewRow();

    default boolean claim(Player player) {
        if (player.level.isClientSide()) { return false; }
        this.setPlayerUUID(player == null ? BLANK_UUID : player.getUUID());
        if (!this.hasRow()) {
            this.getNewRow().insert();
        }
        return true;
    }

    default BlockPartyDB getData() {
        return BlockPartyDB.get(this.getWorld());
    }
}
