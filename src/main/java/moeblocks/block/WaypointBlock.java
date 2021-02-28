package moeblocks.block;

import moeblocks.automata.state.enums.RibbonColor;
import moeblocks.automata.state.enums.TimeOfDay;
import net.minecraft.block.Block;

public class WaypointBlock extends Block {
    protected final RibbonColor color;
    protected final TimeOfDay time;

    public WaypointBlock(Properties properties, RibbonColor color, TimeOfDay time)  {
        super(properties);
        this.color = color;
        this.time = time;
    }

    public WaypointBlock(Properties properties, RibbonColor color) {
        this(properties, color, null);
    }
}
