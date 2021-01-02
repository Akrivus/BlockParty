package moeblocks.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class MoeLeavesBlock extends LeavesBlock {
    public MoeLeavesBlock(MaterialColor color) {
        super(AbstractBlock.Properties.create(Material.LEAVES, color).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid());
    }
}
