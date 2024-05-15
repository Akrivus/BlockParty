package block_party.blocks;

import block_party.blocks.entity.PaperLanternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PaperLanternBlock extends AbstractDataBlock<PaperLanternBlockEntity> {
    protected static final VoxelShape AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    private final MapColor color;

    public PaperLanternBlock(Properties properties, MapColor color) {
        super(PaperLanternBlockEntity::new, properties.lightLevel((state) -> 15));
        this.color = color;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public MapColor defaultMapColor() {
        return this.color;
    }
}
