package moeblocks.block;

import moeblocks.automata.state.enums.RibbonColor;
import moeblocks.automata.state.enums.TimeOfDay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

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

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(world, pos, state, player);
    }
}
