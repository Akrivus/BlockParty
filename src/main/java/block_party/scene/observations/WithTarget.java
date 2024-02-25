package block_party.scene.observations;

import block_party.scene.SceneVariables;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class WithTarget extends AbstractEntity {
    protected String name;

    public WithTarget() {
        this.getter = (npc) -> SceneVariables.getTargets(npc).getEntity(npc.level, this.name);
    }

    public void parse(JsonObject json) {
        this.name = GsonHelper.getAsString(json, "name");
        super.parse(json);
    }

    public static class Player extends WithTarget {
        public Player() {
            this.getter = (npc) -> SceneVariables.getTargets(npc.getServerPlayer()).getEntity(npc.level, this.name);
        }
    }
}
