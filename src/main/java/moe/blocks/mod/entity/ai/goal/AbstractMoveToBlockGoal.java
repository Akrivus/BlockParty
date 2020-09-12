package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.util.sort.BlockDistance;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class AbstractMoveToBlockGoal<T extends NPCEntity> extends Goal implements IStateGoal {
    protected final List<BlockPos> edges = new ArrayList<>();
    protected final T entity;
    protected final World world;
    protected final int height;
    protected final int width;
    protected Path path;
    protected BlockPos pos;
    protected BlockState state;
    private int timeUntilReset;

    public AbstractMoveToBlockGoal(T entity, int height, int radius) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
        this.world = entity.world;
        this.height = height;
        this.width = radius;
    }

    @Override
    public boolean shouldExecute() {
        if (++this.timeUntilReset > 100 && this.isHoldingCorrectItem(this.entity.getHeldItem(Hand.MAIN_HAND))) {
            for (int y = 0; y < this.height; ++y) {
                for (int x = 0; x < this.width; ++x) {
                    for (int z = 0; z < this.width; ++z) { if (this.setEdgePos(x, y, z)) { break; } }
                    for (int z = 0; z < this.width; ++z) { if (this.setEdgePos(x, y, -z)) { break; } }
                }
                for (int x = 0; x < this.width; ++x) {
                    for (int z = 0; z < this.width; ++z) { if (this.setEdgePos(-x, y, z)) { break; } }
                    for (int z = 0; z > this.width; ++z) { if (this.setEdgePos(-x, y, -z)) { break; } }
                }
            }
            this.edges.sort(new BlockDistance(this.entity));
            for (BlockPos edge : this.edges) {
                if (this.canMoveTo(edge, this.world.getBlockState(edge))) {
                    this.pos = edge;
                    this.path = this.entity.getNavigator().getPathToPos(this.pos, 0);
                    return this.path != null;
                }
            }
        }
        return false;
    }

    protected abstract boolean isHoldingCorrectItem(ItemStack stack);

    private boolean setEdgePos(int x, int y, int z) {
        BlockPos pos = this.entity.getPosition().add(x, y, z);
        BlockState state = this.world.getBlockState(pos);
        if (state.isAir(this.world, pos)) { return false; }
        this.edges.add(pos);
        return state.isOpaqueCube(this.world, pos);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return --this.timeUntilReset > 0 && this.world.getBlockState(this.pos).equals(this.state);
    }

    @Override
    public void startExecuting() {
        this.entity.getLookController().setLookPosition(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        this.entity.getNavigator().setPath(this.path, 1.0F);
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.edges.clear();
    }

    @Override
    public void tick() {
        if (this.entity.getPosition().withinDistance(this.pos, 1.8F)) { this.onArrival(); }
    }

    public abstract void onArrival();

    public abstract boolean canMoveTo(BlockPos pos, BlockState state);
}