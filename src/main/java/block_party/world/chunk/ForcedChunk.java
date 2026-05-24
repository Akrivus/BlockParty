package block_party.world.chunk;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.Map;

public final class ForcedChunk {
    private static final int FORCED_CHUNK_DISTANCE = 2;
    private static final TicketType<Long> BLOCK_PARTY_TEMPORARY = TicketType.create("block_party:temporary", Long::compare);
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
        level.getChunkSource().addRegionTicket(BLOCK_PARTY_TEMPORARY, pos, FORCED_CHUNK_DISTANCE, id, true);
        level.getChunk(pos.x, pos.z);
        return level;
    }

    public static void release(long id) {
        ForcedChunk chunk = CHUNKS.remove(id);
        if (chunk != null) {
            chunk.level.getChunkSource().removeRegionTicket(BLOCK_PARTY_TEMPORARY, chunk.pos, FORCED_CHUNK_DISTANCE, id, true);
        }
    }

    public long id() {
        return this.id;
    }

    public ChunkPos pos() {
        return this.pos;
    }
}
