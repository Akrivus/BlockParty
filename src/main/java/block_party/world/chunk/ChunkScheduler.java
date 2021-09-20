package block_party.world.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChunkScheduler {
    private static final Map<Long, ChunkScheduler> chunks = new LinkedHashMap<>();

    private final Level level;
    private final ChunkPos pos;

    public ChunkScheduler(Level level, ChunkPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public void despawn() {
        this.level.getChunkSource().updateChunkForced(this.pos, false);
    }

    public static ChunkScheduler get(long id) {
        return ChunkScheduler.chunks.get(id);
    }

    public static ServerLevel queue(long id, ServerLevel level, ChunkPos pos) {
        if (level != null) {
            ChunkScheduler.chunks.put(id, new ChunkScheduler(level, pos));
            level.tickChunk(level.getChunk(pos.x, pos.z), 20);
            level.getChunkSource().updateChunkForced(pos, true);
            return level;
        } else {
            return null;
        }
    }
}
