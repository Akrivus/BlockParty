package block_party.blocks;

import block_party.blocks.entity.WindChimesBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WindChimesBlock extends AbstractDataBlock<WindChimesBlockEntity> {
    protected static final VoxelShape AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);

    public WindChimesBlock(Properties properties) {
        super(WindChimesBlockEntity::new, properties.lightLevel((state) -> 15));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AABB;
    }
}
