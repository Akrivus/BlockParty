package moeblocks.util;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ChunkScheduler {
    private static final Map<UUID, ChunkScheduler> chunks = new LinkedHashMap<>();
    
    private final World world;
    private final ChunkPos pos;
    
    public ChunkScheduler(World world, ChunkPos pos) {
        this.world = world;
        this.pos = pos;
    }
    
    public void despawn() {
        this.world.getChunkProvider().forceChunk(this.pos, false);
    }
    
    public static ChunkScheduler get(UUID uuid) {
        return ChunkScheduler.chunks.get(uuid);
    }
    
    public static void queue(UUID uuid, ServerWorld world, ChunkPos pos) {
        ChunkScheduler.chunks.put(uuid, new ChunkScheduler(world, pos));
        world.tickEnvironment(world.getChunk(pos.x, pos.z), 20);
        world.getChunkProvider().forceChunk(pos, true);
    }
}
