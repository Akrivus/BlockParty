package block_party.blocks;

import block_party.registry.CustomSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
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
        super(BlockSetType.CHERRY, properties);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : this.getShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST_AABB;
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            default -> NORTH_AABB;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockState next = state.cycle(OPEN);
        level.setBlock(pos, next, 10);
        String sound = next.getValue(OPEN) ? "block.shoji_screen.open" : "block.shoji_screen.close";
        level.playSound(player, pos, CustomSounds.ENTRIES.get(sound).get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }
}
