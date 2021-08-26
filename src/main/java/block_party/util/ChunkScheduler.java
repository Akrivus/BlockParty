package block_party.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ChunkScheduler {
    private static final Map<UUID, ChunkScheduler> chunks = new LinkedHashMap<>();

    private final Level level;
    private final ChunkPos pos;

    public ChunkScheduler(Level world, ChunkPos pos) {
        this.level = world;
        this.pos = pos;
    }

    public void despawn() {
        this.level.getChunkSource().updateChunkForced(this.pos, false);
    }

    public static ChunkScheduler get(UUID id) {
        return ChunkScheduler.chunks.get(id);
    }

    public static ServerLevel queue(UUID id, ServerLevel world, ChunkPos pos) {
        if (world != null) {
            ChunkScheduler.chunks.put(id, new ChunkScheduler(world, pos));
            world.tickChunk(world.getChunk(pos.x, pos.z), 20);
            world.getChunkSource().updateChunkForced(pos, true);
            return world;
        } else {
            return null;
        }
    }
}
