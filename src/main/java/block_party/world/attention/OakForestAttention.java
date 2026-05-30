package block_party.world.attention;

import block_party.entities.Moe;
import block_party.entities.chores.CardinalForestChore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public enum OakForestAttention implements AttentionDefinition {
    INSTANCE;

    public static final String TYPE = "oak_forest";
    public static final String SOURCE = "sapling_drop";

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String source() {
        return SOURCE;
    }

    @Override
    public BlockState cardinalState() {
        return Blocks.OAK_LOG.defaultBlockState();
    }

    @Override
    public boolean matchesDrop(ItemStack stack) {
        return stack.is(Items.OAK_SAPLING);
    }

    @Override
    public boolean matchesBrokenBlock(BlockState state) {
        return state.is(Blocks.OAK_LOG);
    }

    @Override
    public void startChore(Moe moe, BlockPos origin, UUID playerUuid) {
        if (moe.level() instanceof ServerLevel level) {
            moe.chores().start(CardinalForestChore.oakSapling(level, origin, playerUuid));
        }
    }
}
