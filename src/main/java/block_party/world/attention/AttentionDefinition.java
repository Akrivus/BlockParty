package block_party.world.attention;

import block_party.entities.Moe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public interface AttentionDefinition {
    String type();

    String source();

    BlockState cardinalState();

    boolean matchesDrop(ItemStack stack);

    boolean matchesBrokenBlock(BlockState state);

    void startChore(Moe moe, BlockPos origin, UUID playerUuid);
}
