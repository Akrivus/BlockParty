package block_party.registry.resources;

import block_party.BlockParty;
import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;
import block_party.scene.ISceneRequirement;
import block_party.scene.Scene;
import block_party.scene.SceneTrigger;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SceneManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = BlockParty.GSON.create();
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<SceneTrigger, List<Scene>> scenes = ImmutableMap.of();
    private Map<ResourceLocation, Scene> byName = ImmutableMap.of();
    private boolean hasErrors;

    public SceneManager() {
        super(GSON, "scenes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> sceneFolder, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.hasErrors = false;
        Map<SceneTrigger, ImmutableList.Builder<Scene>> map = Maps.newHashMap();
        ImmutableMap.Builder<ResourceLocation, Scene> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : sceneFolder.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "scene");

            SceneTrigger trigger = SceneTrigger.NULL.fromValue(JsonUtils.getAsResourceLocation(json, "trigger"));

            List<ISceneRequirement> requirements = ISceneRequirement.parseArray(json.getAsJsonArray("requirements"));
            List<ISceneAction> actions = ISceneAction.parseArray(json.getAsJsonArray("actions"));

            Scene scene = new Scene(requirements, actions);
            map.computeIfAbsent(trigger, (t) -> ImmutableList.builder()).add(scene);
            builder.put(location, scene);
        }

        this.scenes = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> entry.getValue().build()));
        this.byName = builder.build();
    }

    public Scene get(SceneTrigger trigger, BlockPartyNPC npc) {
        List<Scene> scenes = new ArrayList(this.scenes.getOrDefault(trigger, ImmutableList.of()));
        if (scenes.isEmpty()) { return null; }
        Collections.shuffle(scenes);
        scenes.removeIf((scene) -> !scene.fulfills(npc));
        if (scenes.isEmpty()) { return null; }
        return scenes.get(0);
    }
}

