package block_party.blocks;

import block_party.blocks.entity.AbstractDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class AbstractDataBlock<T extends AbstractDataBlockEntity> extends Block implements EntityBlock {
    private final Supplier<T> initializer;

    protected AbstractDataBlock(Supplier<T> initializer, Properties properties) {
        super(properties);
        this.initializer = initializer;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player) {
            this.getTileEntity(world, pos).claim((Player) (placer));
        }
    }

    public T getTileEntity(Level world, BlockPos pos) {
        return ((T) (world.getBlockEntity(pos)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.initializer.get();
    }
}
