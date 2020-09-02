package moe.blocks.mod.entity.ai.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class FarmlandBehavior extends BasicRandomBehavior {
    @Override
    public void onRandomTick() {
        BlockPos pos = this.moe.getPosition();
        if (this.moe.world.getBlockState(pos.down()).getBlock() == Blocks.DIRT) {
            this.moe.world.setBlockState(pos.down(), this.moe.getBlockState());
        } else if (this.moe.world.isDaytime() && this.moe.world.getLight(pos) < 9) {
            this.moe.setBlockData(Blocks.DIRT.getDefaultState());
        }
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.FARMLAND;
    }
}
