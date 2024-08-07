package block_party.blocks;

import block_party.registry.CustomSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShojiScreenBlock extends DoorBlock {
    public ShojiScreenBlock(Properties properties) {
        super(properties, BlockSetType.CHERRY);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : this.getShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
        default:
        case NORTH:
            return NORTH_AABB;
        case EAST:
            return EAST_AABB;
        case SOUTH:
            return SOUTH_AABB;
        case WEST:
            return WEST_AABB;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        level.setBlock(pos, state = state.cycle(OPEN), 10);
        level.playSound(player, pos, (state.getValue(OPEN) ? CustomSounds.BLOCK_SHOJI_SCREEN_OPEN : CustomSounds.BLOCK_SHOJI_SCREEN_CLOSE).get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
