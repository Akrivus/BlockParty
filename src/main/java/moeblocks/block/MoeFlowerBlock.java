package moeblocks.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.potion.Effect;

public class MoeFlowerBlock extends FlowerBlock {
    public MoeFlowerBlock(Effect effect) {
        super(effect, effect.isBeneficial() ? 18 : 21, AbstractBlock.Properties.from(Blocks.POPPY));
    }
}
