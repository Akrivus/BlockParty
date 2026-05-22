package block_party.world.chunk;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;

public class ForcedChunk {
    private static final Map<Long, ForcedChunk> chunks = Maps.newLinkedHashMap();

    private final long id;
    private final Level level;
    private final ChunkPos pos;

    public ForcedChunk(long id, Level level, ChunkPos pos) {
        this.id = id;
        this.level = level;
        this.pos = pos;
    }

    public void despawn() {
        this.level.getChunkSource().updateChunkForced(this.pos, false);
        ForcedChunk.chunks.remove(this.id, this);
    }

    public static ForcedChunk get(long id) {
        return ForcedChunk.chunks.get(id);
    }

    public static void release(long id) {
        ForcedChunk chunk = ForcedChunk.get(id);
        if (chunk != null) {
            chunk.despawn();
        }
    }

    public static ServerLevel queue(long id, ServerLevel level, ChunkPos pos) {
        if (level != null) {
            ForcedChunk.chunks.put(id, new ForcedChunk(id, level, pos));
            level.tickChunk(level.getChunk(pos.x, pos.z), 20);
            level.getChunkSource().updateChunkForced(pos, true);
            return level;
        } else {
            return null;
        }
    }
}
