package block_party.db;

import block_party.db.sql.Row;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface Recordable<M extends Row> {
    UUID BLANK_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    default boolean claim(Player player) {
        if (player.level.isClientSide()) { return false; }
        this.setPlayerUUID(player == null ? BLANK_UUID : player.getUUID());
        if (!this.hasRow()) {
            this.getNewRow().insert();
        }
        return true;
    }

    boolean hasRow();

    M getNewRow();

    default BlockPartyDB getData() {
        return BlockPartyDB.get(this.getWorld());
    }

    Level getWorld();

    DimBlockPos getDimBlockPos();

    long getDatabaseID();

    void setDatabaseID(long id);

    UUID getPlayerUUID();

    void setPlayerUUID(UUID uuid);

    M getRow();
}
