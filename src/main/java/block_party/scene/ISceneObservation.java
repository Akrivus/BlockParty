package block_party.scene;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.registry.resources.Scenes;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface ISceneObservation {
    boolean verify(BlockPartyNPC npc);

    static List<ISceneObservation> parseArray(JsonArray array) {
        ImmutableList.Builder<ISceneObservation> filters = ImmutableList.builder();
        if (array == null) { return filters.build(); }
        for (int i = 0; i < array.size(); ++i) {
            JsonElement member = array.get(i);
            ResourceLocation location = null;
            if (member.isJsonObject() && member.getAsJsonObject().has("type")) { location = ResourceLocation.tryParse(member.getAsJsonObject().get("type").getAsString()); }
            if (member.isJsonPrimitive()) { location = ResourceLocation.tryParse(member.getAsString()); }
            if (location == null) { continue; }
            ISceneObservation filter = buildKnownFilter(Scenes.own(location));
            if (filter == null) { continue; }
            if (member.isJsonObject() && member.getAsJsonObject().has("filter")) { filter.parse(member.getAsJsonObject().getAsJsonObject("filter")); }
            filters.add(filter);
        }
        return filters.build();
    }

    static ISceneObservation buildKnownFilter(ResourceLocation location) {
        if (JsonUtils.SCENE_ALWAYS.equals(location)) {
            return SceneObservation.ALWAYS;
        }
        return null;
    }

    default void parse(JsonObject json) { }
}
