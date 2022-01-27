package block_party.registry.resources;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.registry.CustomResources;
import block_party.scene.Scene;
import block_party.scene.SceneTrigger;
import block_party.scene.filters.traits.Gender;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class Names extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = BlockParty.GSON.create();
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<Gender, List<String>> map = ImmutableMap.of();
    private boolean hasErrors;

    public Names() {
        super(GSON, "moes/names");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> folder, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.hasErrors = false;
        ImmutableMap.Builder<Gender, List<String>> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : folder.entrySet()) {
            ResourceLocation location = entry.getKey();
            try {
                Gender gender = Gender.FEMALE.fromValue(location);
                ImmutableList.Builder<String> names = new ImmutableList.Builder<>();
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "names");
                JsonArray array = json.getAsJsonArray("names");
                for (int i = 0; i < array.size(); ++i)
                    names.add(array.get(i).getAsString());
                builder.put(gender, names.build());
            } catch (JsonSyntaxException e) {
                LOGGER.error("Parsing error loading names for: {}", location);
            }
        }

        this.map = builder.build();
    }

    public static List<String> get(Gender gender) {
        return CustomResources.NAMES.map.get(gender);
    }
}
