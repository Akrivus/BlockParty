package block_party.registry.resources;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.registry.CustomResources;
import block_party.registry.CustomTags;
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

    private Map<Block, Map<BlockStatePattern, ResourceLocation>> map = ImmutableMap.of();
    private boolean hasErrors;

    public MoeTextures() {
        super(GSON, "moes/textures");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> folder, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.hasErrors = false;
        Map<Block, ImmutableMap.Builder<BlockStatePattern, ResourceLocation>> map = Maps.newHashMap();

        for (Map.Entry<ResourceLocation, JsonElement> entry : folder.entrySet()) {
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

            BlockStatePattern pattern = new BlockStatePattern(state, builder.build());
            ResourceLocation texture = JsonUtils.getAsResourceLocation(json, "texture");
            map.computeIfAbsent(block, (b) -> ImmutableMap.builder()).put(pattern, texture);
        }

        this.map = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> entry.getValue().build()));
    }

    public static ResourceLocation get(BlockPartyNPC npc) {
        BlockState state = npc.getVisibleBlockState();
        Map<BlockStatePattern, ResourceLocation> textures = CustomResources.MOE_TEXTURES.map.getOrDefault(state, ImmutableMap.of(new BlockStatePattern(state, ImmutableMap.of()), getDefaultPathFor(state)));
        for (BlockStatePattern pattern : textures.keySet()) {
            if (pattern.matches(npc.getActualBlockState())) { return textures.get(pattern); }
        }
        return getDefaultPathFor(state);
    }

    private static ResourceLocation getDefaultPathFor(BlockState state) {
        ResourceLocation location = state.getBlock().getRegistryName();
        String file = location.getPath();
        if (state.is(CustomTags.HAS_FESTIVE_TEXTURES)) {
            file += ".christmas";
        }
        String path = String.format("textures/moe/%s.png", file);
        return new ResourceLocation(location.getNamespace(), path);
    }

    record BlockStatePattern(BlockState state, Map<Property<?>, Comparable<?>> props) {
        public boolean matches(BlockState state) {
            if (this.state.getBlock() != state.getBlock()) { return false; }
            for (Property<?> prop : this.props.keySet()) {
                if (this.props.get(prop) != state.getValue(prop)) { return false; }
            }
            return true;
        }
    }
}