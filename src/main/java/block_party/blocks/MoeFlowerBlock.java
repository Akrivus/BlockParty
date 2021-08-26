package block_party.blocks;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.FlowerBlock;

public class MoeFlowerBlock extends FlowerBlock {
    public MoeFlowerBlock(Properties properties, MobEffect effect) {
        super(effect, effect.isBeneficial() ? 18 : 21, properties);
    }
}
