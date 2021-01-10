package moeblocks.block;

import net.minecraft.block.FlowerBlock;
import net.minecraft.potion.Effect;

public class MoeFlowerBlock extends FlowerBlock {
    public MoeFlowerBlock(Properties properties, Effect effect) {
        super(effect, effect.isBeneficial() ? 18 : 21, properties);
    }
}
