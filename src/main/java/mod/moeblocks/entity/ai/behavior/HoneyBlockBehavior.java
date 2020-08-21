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

public class HoneyBlockBehavior extends BasicBehavior {
    @Override
    public void tick() {
        this.moe.world.addParticle(ParticleTypes.DRIPPING_HONEY, this.moe.getCenteredRandomPosX(), this.moe.getPosYRandom(), this.moe.getCenteredRandomPosZ(), 0.0D, 0.0D, 0.0D);
        super.tick();
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.HONEY_BLOCK;
    }
}
