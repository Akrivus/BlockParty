package block_party.world.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.Map;

public class ForcedChunk {
    private static final Map<Long, ForcedChunk> chunks = new LinkedHashMap<>();

    private final Level level;
    private final ChunkPos pos;

    public ForcedChunk(Level level, ChunkPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public void despawn() {
        this.level.getChunkSource().updateChunkForced(this.pos, false);
    }

    public static ForcedChunk get(long id) {
        return ForcedChunk.chunks.get(id);
    }

    public static ServerLevel queue(long id, ServerLevel level, ChunkPos pos) {
        if (level != null) {
            ForcedChunk.chunks.put(id, new ForcedChunk(level, pos));
            level.tickChunk(level.getChunk(pos.x, pos.z), 20);
            level.getChunkSource().updateChunkForced(pos, true);
            return level;
        } else {
            return null;
        }
    }
}
