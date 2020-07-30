package mod.moeblocks.entity.ai.behavior;

import com.google.common.collect.Lists;
import mod.moeblocks.entity.util.Behaviors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Queue;

public class WetSpongeBehavior extends BasicBehavior {
    @Override
    public void tick() {
        if (this.moe.world.getDimension().doesWaterVaporize() || this.moe.isBurning()) {
            this.moe.world.addParticle(ParticleTypes.CLOUD, this.moe.getPosXRandom(0.375D), this.moe.getPosYRandom(), this.moe.getPosZRandom(0.375D), this.moe.world.rand.nextGaussian() * 0.02D, this.moe.world.rand.nextGaussian() * 0.02D, this.moe.world.rand.nextGaussian() * 0.02D);
            if (this.extinguish(this.moe.world, this.moe.getPosition())) {
                this.moe.setBlockData(Blocks.SPONGE.getDefaultState());
            }
        } else {
            this.moe.world.addParticle(ParticleTypes.DRIPPING_WATER, this.moe.getPosXRandom(0.375D), this.moe.getPosYRandom(), this.moe.getPosZRandom(0.375D), 0.0D, 0.0D, 0.0D);
        }
    }

    private boolean extinguish(World world, BlockPos pos) {
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
                if (state.getBlock() == Blocks.FIRE) {
                    world.setBlockState(check, Blocks.AIR.getDefaultState(), 3);
                    if (j < 6) {
                        queue.add(new Tuple(check, Integer.valueOf(j + 1)));
                    }
                    ++i;
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
        return Behaviors.WET_SPONGE;
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem() == Items.BUCKET) {
            this.moe.setBlockData(Blocks.SPONGE.getDefaultState());
            ItemStack bucket = new ItemStack(Items.WATER_BUCKET);
            stack.shrink(1);
            if (!player.addItemStackToInventory(bucket)) {
                player.dropItem(bucket, false);
            }
            return true;
        }
        return false;
    }
}
