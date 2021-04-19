package moeblocks.block;

import moeblocks.block.entity.GardenLanternTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GardenLanternBlock extends Block implements ITileEntityProvider {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public GardenLanternBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(LIT, true));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        this.getTileEntity(world, pos).claim((PlayerEntity) (placer));
    }

    public GardenLanternTileEntity getTileEntity(World world, BlockPos pos) {
        return ((GardenLanternTileEntity) (world.getTileEntity(pos)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new GardenLanternTileEntity();
    }
}
