package moeblocks.block;

import moeblocks.block.entity.AbstractDataTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class AbstractDataBlock<T extends AbstractDataTileEntity> extends Block implements ITileEntityProvider {
    private final Supplier<T> initializer;

    protected AbstractDataBlock(Supplier<T> initializer, Properties properties) {
        super(properties);
        this.initializer = initializer;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof PlayerEntity) {
            this.getTileEntity(world, pos).claim((PlayerEntity) (placer));
        }
    }

    public T getTileEntity(World world, BlockPos pos) {
        return ((T) (world.getTileEntity(pos)));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return this.initializer.get();
    }
}
