package block_party.registry.resources;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;

public class MoeTextures extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = BlockParty.GSON.create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static MoeTextures instance;

    private Map<Block, Map<BlockStatePattern, ResourceLocation>> map = ImmutableMap.of();
    private boolean hasErrors;

    public MoeTextures() {
        super(GSON, "moes/textures");
        if (MoeTextures.instance != null) { LOGGER.warn("DollTextures was already instantiated; overwriting."); }
        MoeTextures.instance = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> sceneFolder, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.hasErrors = false;
        Map<Block, ImmutableMap.Builder<BlockStatePattern, ResourceLocation>> map = Maps.newHashMap();

        for (Map.Entry<ResourceLocation, JsonElement> entry : sceneFolder.entrySet()) {
            JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "texture");

            ImmutableMap.Builder<Property<?>, Comparable<?>> builder = ImmutableMap.builder();

            ResourceLocation location = JsonUtils.getAsResourceLocation(json, "block");
            Block block = JsonUtils.getAs(JsonUtils.BLOCK, location);
            BlockState state = block.defaultBlockState();
            Collection<Property<?>> props = state.getProperties();

            JsonArray array = json.getAsJsonArray("props");
            for (int i = 0; i < array.size(); ++i) {
                JsonElement member = array.get(i);
                JsonObject object = member.getAsJsonObject();
                String name = GsonHelper.getAsString(object, "name");

                for (Property<?> prop : props) {
                    if (!prop.getName().equals(name)) { continue; }
                    Comparable<?> value = GSON.fromJson(object.get("value"), prop.getValueClass());
                    builder.put(prop, value);
                }
            }

            BlockStatePattern pattern = new BlockStatePattern(block, builder.build());
            ResourceLocation texture = JsonUtils.getAsResourceLocation(json, "texture");
            map.computeIfAbsent(block, (b) -> ImmutableMap.builder()).put(pattern, texture);
        }

        this.map = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> entry.getValue().build()));
    }

    public static ResourceLocation get(BlockPartyNPC npc) {
        Block block = npc.getBlock();
        Map<BlockStatePattern, ResourceLocation> textures = MoeTextures.instance.map.getOrDefault(block, ImmutableMap.of(new BlockStatePattern(block, ImmutableMap.of()), getDefaultPathFor(block)));
        for (BlockStatePattern pattern : textures.keySet()) {
            if (pattern.matches(npc.getActualBlockState())) { return textures.get(pattern); }
        }
        return getDefaultPathFor(block);
    }

    private static ResourceLocation getDefaultPathFor(Block block) {
        ResourceLocation location = block.getRegistryName();
        String path = String.format("textures/moe/%s.png", location.getPath());
        return new ResourceLocation(location.getNamespace(), path);
    }

    record BlockStatePattern(Block block, Map<Property<?>, Comparable<?>> props) {
        public boolean matches(BlockState state) {
            if (this.block != state.getBlock()) { return false; }
            for (Property<?> prop : this.props.keySet()) {
                if (this.props.get(prop) != state.getValue(prop)) { return false; }
            }
            return true;
        }
    }
}