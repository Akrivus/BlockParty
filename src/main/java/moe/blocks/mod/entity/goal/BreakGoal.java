package moe.blocks.mod.entity.goal;

import moe.blocks.mod.entity.FiniteEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BreakGoal extends Goal {
    protected final List<BlockPos> edges = new ArrayList<>();
    protected final FiniteEntity entity;
    protected final World world;
    protected final int height;
    protected Path path;
    protected BlockPos pos;
    protected BlockState state;
    private int timeUntilReset;
    private int timeUntilBreak;

    public BreakGoal(FiniteEntity entity, int height) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
        this.world = entity.world;
        this.height = height;
    }

    @Override
    public boolean shouldExecute() {
        if (++this.timeUntilReset > 200) {
            for (int y = -1; y < this.height - 1; ++y) {
                for (int x = -8; x < 8; ++x) {
                    boolean started = false;
                    for (int z = -8; z < 8; ++z) {
                        BlockPos caret = this.entity.getPosition().add(x, y, z);
                        if (this.world.isAirBlock(caret) && !started) {
                            this.edges.add(caret.north());
                            started = true;
                        } else if (started) {
                            started = this.world.isAirBlock(caret);
                            if (!started) {
                                this.edges.add(caret);
                            }
                        }
                    }
                }
            }
            for (BlockPos edge : this.edges) {
                this.state = this.world.getBlockState(edge);
                if (this.canBreakBlock(edge, this.state)) {
                    this.path = this.entity.getNavigator().getPathToPos(this.pos = edge, 1);
                    if (this.path != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean canBreakBlock(BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return --this.timeUntilReset > 0 && this.entity.hasPath() && this.world.getBlockState(this.pos).equals(this.state);
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
        if (--this.timeUntilBreak < 0 && this.entity.getPosition().withinDistance(this.pos, 2.0F)) {
            if (this.world.destroyBlock(this.pos, true, this.entity)) {
                this.entity.swing(Hand.MAIN_HAND, true);
                this.timeUntilBreak = this.entity.getAttackCooldown();
                this.timeUntilReset = 200;
            }
        } else {
            this.entity.getNavigator().setPath(this.path, 1.0F);
        }
    }
}