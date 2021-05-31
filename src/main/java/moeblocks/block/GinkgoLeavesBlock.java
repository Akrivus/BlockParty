package moeblocks.block;

import moeblocks.particle.SakuraParticle;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import java.util.Random;

public class GinkgoLeavesBlock extends LeavesBlock {
    private final RegistryObject<BasicParticleType> particle;

    public GinkgoLeavesBlock(RegistryObject<BasicParticleType> particle, Properties properties) {
        super(properties.setAllowsSpawn((state, reader, pos, entity) -> false).setSuffocates((state, reader, pos) -> false).setBlocksVision((state, reader, pos) -> false));
        this.particle = particle;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);
        SakuraParticle.add(this.particle, world, pos, random);
    }
}
