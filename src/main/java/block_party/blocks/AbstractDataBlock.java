package block_party.blocks;

import block_party.blocks.entity.AbstractDataBlockEntity;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AbstractDataBlock extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, ? extends AbstractDataBlockEntity> initializer;

    public AbstractDataBlock(BiFunction<BlockPos, BlockState, ? extends AbstractDataBlockEntity> initializer, Properties properties) {
        super(properties);
        this.initializer = initializer;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide() && placer instanceof Player player && level.getBlockEntity(pos) instanceof AbstractDataBlockEntity data) {
            data.claim(player);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide() && level.getBlockEntity(pos) instanceof AbstractDataBlockEntity data) {
            data.onDestroyed();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.initializer.apply(pos, state);
    }
}
