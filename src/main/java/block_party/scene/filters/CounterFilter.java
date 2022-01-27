package block_party.scene.filters;

import block_party.world.PlayerData;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class CounterFilter extends IntegerFilter {
    protected String name;

    public CounterFilter() {
        this.function = (npc) -> npc.sceneManager.counters.get(this.name);
    }

    @Override
    public void parse(JsonObject json) {
        this.name = GsonHelper.getAsString(json, "name");
        super.parse(json);
    }

    public static class Player extends CounterFilter {
        public Player() {
            this.function = (npc) -> PlayerData.getCountersFor(npc.getServerPlayer()).get(this.name);
        }
    }
}
