package block_party.scene;

import block_party.entities.BlockPartyNPC;
import block_party.registry.SceneActions;
import block_party.registry.resources.Scenes;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface ISceneAction {
    default void onComplete(BlockPartyNPC npc) { }

    void apply(BlockPartyNPC npc);

    boolean isComplete(BlockPartyNPC npc);

    static List<ISceneAction> parseArray(JsonArray array) {
        ImmutableList.Builder<ISceneAction> actions = ImmutableList.builder();
        if (array == null) { return actions.build(); }
        for (int i = 0; i < array.size(); ++i) {
            JsonElement member = array.get(i);
            ResourceLocation location = null;
            if (member.isJsonObject() && member.getAsJsonObject().has("type")) { location = ResourceLocation.tryParse(member.getAsJsonObject().get("type").getAsString()); }
            if (member.isJsonPrimitive()) { location = ResourceLocation.tryParse(member.getAsString()); }
            if (location == null) { continue; }
            SceneActions.Builder builder = JsonUtils.getAs(JsonUtils.SCENE_ACTION, Scenes.own(location));
            if (builder == null) { continue; }
            ISceneAction action = builder.build();
            if (member.isJsonObject() && member.getAsJsonObject().has("action")) { action.parse(member.getAsJsonObject().getAsJsonObject("action")); }
            actions.add(action);
        }
        return actions.build();
    }

    default void parse(JsonObject json) { }

    default ISceneAction copy() { return this; }
}
