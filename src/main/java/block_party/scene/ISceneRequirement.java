package block_party.scene;

import block_party.npc.BlockPartyNPC;
import block_party.registry.SceneRequirements;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface ISceneRequirement {
    boolean verify(BlockPartyNPC npc);

    static List<ISceneRequirement> parseArray(JsonArray array) {
        ImmutableList.Builder<ISceneRequirement> requirements = ImmutableList.builder();
        for (int i = 0; i < array.size(); ++i) {
            JsonElement member = array.get(i);
            ResourceLocation location = null;
            if (member.isJsonObject()) { location = JsonUtils.getAsResourceLocation(member.getAsJsonObject(), "type"); }
            if (member.isJsonPrimitive()) { location = new ResourceLocation(member.getAsString()); }
            if (location == null) { continue; }
            ISceneRequirement requirement = JsonUtils.<SceneRequirements.Factory>getAs(JsonUtils.SCENE_REQUIREMENT, location).get();
            if (member.isJsonObject()) { requirement.parse(member.getAsJsonObject().getAsJsonObject("requirement")); }
            requirements.add(requirement);
        }
        return requirements.build();
    }

    default void parse(JsonObject json) { }

}
