package block_party.world.attention;

import block_party.entities.Moe;
import block_party.entities.chores.CardinalForestChore;
import block_party.entities.chores.PlaceBlockChores;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public enum WoodForestAttention implements AttentionDefinition {
    OAK("oak_forest", Blocks.OAK_LOG, PlaceBlockChores.Config.OAK_SAPLING),
    BIRCH("birch_forest", Blocks.BIRCH_LOG, PlaceBlockChores.Config.BIRCH_SAPLING),
    SPRUCE("spruce_forest", Blocks.SPRUCE_LOG, PlaceBlockChores.Config.SPRUCE_SAPLING),
    JUNGLE("jungle_forest", Blocks.JUNGLE_LOG, PlaceBlockChores.Config.JUNGLE_SAPLING),
    ACACIA("acacia_forest", Blocks.ACACIA_LOG, PlaceBlockChores.Config.ACACIA_SAPLING),
    DARK_OAK("dark_oak_forest", Blocks.DARK_OAK_LOG, PlaceBlockChores.Config.DARK_OAK_SAPLING);

    public static final String SOURCE = "sapling_drop";

    private final String type;
    private final Block log;
    private final PlaceBlockChores.Config config;

    WoodForestAttention(String type, Block log, PlaceBlockChores.Config config) {
        this.type = type;
        this.log = log;
        this.config = config;
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public String source() {
        return SOURCE;
    }

    @Override
    public BlockState cardinalState() {
        return this.log.defaultBlockState();
    }

    @Override
    public boolean matchesDrop(ItemStack stack) {
        return stack.is(this.config.item());
    }

    @Override
    public boolean matchesBrokenBlock(BlockState state) {
        return state.is(this.log);
    }

    @Override
    public void startChore(Moe moe, BlockPos origin, UUID playerUuid) {
        if (moe.level() instanceof ServerLevel level) {
            moe.chores().start(CardinalForestChore.sapling(level, origin, playerUuid, this.config));
        }
    }
}
