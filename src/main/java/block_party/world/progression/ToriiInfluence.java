package block_party.world.progression;

import block_party.db.BlockPartyDB;
import block_party.db.records.Shrine;
import java.sql.SQLException;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** Server-side counterpart to the shrine sky and firefly influence range. */
public final class ToriiInfluence {
    public static final int MAX_HORIZONTAL_DISTANCE = 2048;

    private ToriiInfluence() {
    }

    public static boolean contains(ServerLevel level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        try {
            for (Shrine shrine : BlockPartyDB.get(level).listShrineRows(level.dimension())) {
                BlockPos shrinePos = shrine.dimPos().getPos();
                int distance = Math.abs(pos.getX() - shrinePos.getX()) + Math.abs(pos.getZ() - shrinePos.getZ());
                if (distance <= MAX_HORIZONTAL_DISTANCE) {
                    return true;
                }
            }
        } catch (SQLException exception) {
            return false;
        }
        return false;
    }
}
