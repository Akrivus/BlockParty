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
        for (int i = 0; i < array.size(); ++i) {
            JsonElement member = array.get(i);
            ResourceLocation location = null;
            if (member.isJsonObject()) { location = JsonUtils.getAsResourceLocation(member.getAsJsonObject(), "type"); }
            if (member.isJsonPrimitive()) { location = new ResourceLocation(member.getAsString()); }
            if (location == null) { continue; }
            ISceneAction action = JsonUtils.<SceneActions.Builder>getAs(JsonUtils.SCENE_ACTION, Scenes.own(location)).build();
            if (member.isJsonObject()) { action.parse(member.getAsJsonObject().getAsJsonObject("action")); }
            actions.add(action);
        }
        return actions.build();
    }

    default void parse(JsonObject json) { }

    default ISceneAction copy() { return this; }
}
