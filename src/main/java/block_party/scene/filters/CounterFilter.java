package block_party.scene.filters;

import block_party.world.PlayerData;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class CounterFilter extends NumberFilter {
    protected String counter;

    public CounterFilter() {
        this.function = (npc) -> npc.automaton.getCounter(this.counter);
    }

    @Override
    public void parse(JsonObject json) {
        this.counter = GsonHelper.getAsString(json, "counter");
        super.parse(json);
    }

    public static class Player extends CounterFilter {
        public Player() {
            this.function = (npc) -> {
                ServerPlayer player = (ServerPlayer) npc.getPlayer();
                return PlayerData.getCountersFor(player).get(this.counter);
            };
        }
    }
}
