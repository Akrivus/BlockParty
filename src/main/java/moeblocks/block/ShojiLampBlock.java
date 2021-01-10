package moeblocks.block;

import net.minecraft.block.Block;

public class ShojiLampBlock extends Block {
    public ShojiLampBlock(Properties properties) {
        super(properties.setLightLevel((state) -> 15));
    }
}
