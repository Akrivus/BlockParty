package moeblocks.entity.ai.goal;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.automata.IStateGoal;
import moeblocks.util.sort.BlockDistance;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class AbstractMoveToBlockGoal<T extends AbstractNPCEntity> extends Goal implements IStateGoal {
    protected final List<BlockPos> edges = new ArrayList<>();
    protected final T entity;
    protected final World world;
    protected final int height;
    protected final int width;
    protected int timeUntilNextMove = 100;
    protected int timeUntilReset;
    protected Path path;
    protected BlockPos pos;
    protected BlockState state;

    public AbstractMoveToBlockGoal(T entity, int height, int radius) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        this.entity = entity;
        this.world = entity.world;
        this.height = height;
        this.width = radius;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.isThreadSafe()) { return false; }
        if (++this.timeUntilReset > this.timeUntilNextMove) {
            this.world.getProfiler().startSection("findBlocks");
            for (int y = 0; y < this.height; ++y) {
                for (int x = 0; x < this.width; ++x) {
                    for (int z = 0; z < this.width; ++z) { if (this.setEdgePos(x, y, -z)) { break; } }
                    for (int z = 0; z < this.width; ++z) { if (this.setEdgePos(x, y, z)) { break; } }
                }
                for (int x = 0; x < this.width; ++x) {
                    for (int z = 0; z > this.width; ++z) { if (this.setEdgePos(-x, y, -z)) { break; } }
                    for (int z = 0; z < this.width; ++z) { if (this.setEdgePos(-x, y, z)) { break; } }
                }
            }
            this.edges.sort(new BlockDistance(this.entity));
            for (BlockPos edge : this.edges) {
                if (this.canMoveTo(edge, this.world.getBlockState(edge))) {
                    this.path = this.entity.getNavigator().getPathToPos((this.pos = edge), 0);
                    if (this.path != null) { break; }
                }
            }
            this.world.getProfiler().endSection();
        }
        this.edges.clear();
        return this.path != null;
    }

    private boolean setEdgePos(int x, int y, int z) {
        BlockPos pos = this.entity.getPosition().add(x, y, z);
        IChunk chunk = this.entity.getChunk(new ChunkPos(pos));
        if (chunk == null) { return false; }
        BlockState state = chunk.getBlockState(pos);
        if (state.isAir(chunk, pos)) { return false; }
        this.edges.add(pos);
        return state.isOpaqueCube(chunk, pos);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.isThreadSafe() && --this.timeUntilReset > 0 && this.entity.hasPath();
    }

    @Override
    public void startExecuting() {
        if (!this.entity.isThreadSafe()) { return; }
        this.entity.getLookController().setLookPosition(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        this.entity.getNavigator().setPath(this.path, 1.0F);
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.path = null;
    }

    @Override
    public void tick() {
        if (this.entity.isThreadSafe() && this.entity.getPosition().withinDistance(this.pos, this.entity.getBlockStrikingDistance())) { this.onArrival(); }
    }

    public abstract void onArrival();

    public abstract boolean canMoveTo(BlockPos pos, BlockState state);
}