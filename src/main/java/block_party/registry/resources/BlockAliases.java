package block_party.registry.resources;

import block_party.BlockParty;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class BlockAliases extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = BlockParty.GSON.create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static BlockAliases instance;

    private Map<Block, Block> map = ImmutableMap.of();
    private boolean hasErrors;

    public BlockAliases() {
        super(GSON, "dolls/aliases");
        if (BlockAliases.instance != null) { LOGGER.warn("BlockAliases was already instantiated; overwriting."); }
        BlockAliases.instance = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> sceneFolder, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.hasErrors = false;
        ImmutableMap.Builder<Block, Block> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : sceneFolder.entrySet()) {
            ResourceLocation location = entry.getKey();
            try {
                Block alias = JsonUtils.getAs(JsonUtils.BLOCK, location);
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "aliases");

                JsonArray array = json.getAsJsonArray("aliases");
                for (int i = 0; i < array.size(); ++i) {
                    JsonElement element = array.get(i);
                    ResourceLocation name = new ResourceLocation(element.getAsString());
                    try {
                        Block block = JsonUtils.getAs(JsonUtils.BLOCK, name);
                        builder.put(block, alias);
                    } catch (JsonSyntaxException e) {
                        LOGGER.error("Parsing error loading block alias of {}: {}", location, name);
                    }
                }
            } catch (JsonSyntaxException e) {
                LOGGER.error("Parsing error loading block alias: {}", location);
            }
        }

        this.map = builder.build();
    }

    public static BlockState get(BlockState state) {
        Block block = state.getBlock();
        Block alias = BlockAliases.instance.map.getOrDefault(block, block);
        return alias.defaultBlockState();
    }
}
