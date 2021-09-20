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

import java.util.function.BiFunction;

public class AbstractDataBlock<T extends AbstractDataBlockEntity> extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, T> initializer;

    protected AbstractDataBlock(BiFunction<BlockPos, BlockState, T> initializer, Properties properties) {
        super(properties);
        this.initializer = initializer;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player) {
            this.getTileEntity(level, pos).claim((Player) (placer));
        }
    }

    public T getTileEntity(Level level, BlockPos pos) {
        return ((T) (level.getBlockEntity(pos)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.initializer.apply(pos, state);
    }
}
