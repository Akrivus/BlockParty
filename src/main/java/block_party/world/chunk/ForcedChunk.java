package block_party.world.chunk;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Map;

public final class ForcedChunk {
    private static final Map<Long, ForcedChunk> CHUNKS = Maps.newLinkedHashMap();

    private final long id;
    private final ServerLevel level;
    private final ChunkPos pos;

    private ForcedChunk(long id, ServerLevel level, ChunkPos pos) {
        this.id = id;
        this.level = level;
        this.pos = pos;
    }

    public static ForcedChunk get(long id) {
        return CHUNKS.get(id);
    }

    public static ServerLevel queue(long id, ServerLevel level, ChunkPos pos) {
        if (level == null) {
            return null;
        }
        release(id);
        ForcedChunk chunk = new ForcedChunk(id, level, pos);
        CHUNKS.put(id, chunk);
        level.getChunk(pos.x, pos.z);
        level.getChunkSource().updateChunkForced(pos, true);
        return level;
    }

    public static void release(long id) {
        ForcedChunk chunk = CHUNKS.remove(id);
        if (chunk != null) {
            chunk.level.getChunkSource().updateChunkForced(chunk.pos, false);
        }
    }

    public long id() {
        return this.id;
    }

    public ChunkPos pos() {
        return this.pos;
    }
}
