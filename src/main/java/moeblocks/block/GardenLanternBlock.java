package moeblocks.block;

import moeblocks.block.entity.GardenLanternTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;

public class GardenLanternBlock extends AbstractDataBlock<GardenLanternTileEntity> {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public GardenLanternBlock(Properties properties) {
        super(GardenLanternTileEntity::new, properties);
        this.setDefaultState(this.getDefaultState().with(LIT, true));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}
