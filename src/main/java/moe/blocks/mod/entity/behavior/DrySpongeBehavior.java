package moe.blocks.mod.entity.behavior;

import com.google.common.collect.Lists;
import moe.blocks.mod.entity.util.Behaviors;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Queue;

public class DrySpongeBehavior extends BasicBehavior {
    @Override
    public void tick() {
        if (this.moe.isInWater() && this.absorb(this.moe.world, this.moe.getPosition())) {
            this.moe.setBlockData(Blocks.WET_SPONGE.getDefaultState());
        }
    }

    private boolean absorb(World world, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple(pos, Integer.valueOf(0)));
        int i = 0;
        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos test = tuple.getA();
            int j = tuple.getB();
            for (Direction direction : Direction.values()) {
                BlockPos check = test.offset(direction);
                BlockState state = world.getBlockState(check);
                Material material = state.getMaterial();
                if (world.getFluidState(check).isTagged(FluidTags.WATER)) {
                    if (state.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler) state.getBlock()).pickupFluid(world, check, state) != Fluids.EMPTY) {
                        if (j < 6) {
                            queue.add(new Tuple(check, Integer.valueOf(j + 1)));
                        }
                        ++i;
                    } else if (state.getBlock() instanceof FlowingFluidBlock) {
                        world.setBlockState(check, Blocks.AIR.getDefaultState(), 3);
                        if (j < 6) {
                            queue.add(new Tuple(check, Integer.valueOf(j + 1)));
                        }
                        ++i;
                    } else if (material == Material.OCEAN_PLANT || material == Material.SEA_GRASS) {
                        Block.spawnDrops(state, world, check, world.getTileEntity(check));
                        world.setBlockState(check, Blocks.AIR.getDefaultState(), 3);
                        if (j < 6) {
                            queue.add(new Tuple(check, Integer.valueOf(j + 1)));
                        }
                        ++i;
                    }
                }
            }
            if (i > 64) {
                break;
            }
        }
        return (i >= 0);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.DRY_SPONGE;
    }
}
