package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import com.google.gson.JsonObject;
import java.util.Locale;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record TakeItemAction(JsonObject matcher, int count, Source source, Destination destination) implements SceneAction {
    public TakeItemAction {
        matcher = matcher == null ? new JsonObject() : matcher.deepCopy();
        count = Math.max(1, count);
        source = source == null ? Source.PLAYER : source;
        destination = destination == null ? Destination.MOE : destination;
    }

    @Override
    public void apply(Moe moe) {
        Container sourceInventory = this.sourceInventory(moe);
        if (sourceInventory == null || !SceneItemStacks.has(sourceInventory, this.matcher)) {
            return;
        }
        ItemStack representative = SceneItemStacks.firstMatch(sourceInventory, this.matcher);
        int transferable = this.destination == Destination.MOE
                ? Math.min(this.count, SceneItemStacks.freeSpace(moe.getInventory(), representative))
                : this.count;
        if (transferable <= 0) {
            return;
        }
        ItemStack removed = SceneItemStacks.remove(sourceInventory, this.matcher, transferable);
        if (removed.isEmpty()) {
            return;
        }
        switch (this.destination) {
            case MOE -> {
                int inserted = SceneItemStacks.insert(moe.getInventory(), removed);
                if (inserted > 0) {
                    moe.receiveGift(removed.copyWithCount(inserted));
                }
            }
            case PLAYER -> {
                Player player = SceneActionPlayers.targetPlayer(moe);
                if (player != null) {
                    player.getInventory().add(removed);
                }
            }
            case DISCARD -> {
            }
        }
    }

    private Container sourceInventory(Moe moe) {
        if (this.source == Source.MOE) {
            return moe.getInventory();
        }
        Player player = SceneActionPlayers.targetPlayer(moe);
        return player == null ? null : player.getInventory();
    }

    public enum Source {
        PLAYER,
        MOE;

        public static Source fromValue(String value) {
            try {
                return Source.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return PLAYER;
            }
        }
    }

    public enum Destination {
        MOE,
        PLAYER,
        DISCARD;

        public static Destination fromValue(String value) {
            try {
                return Destination.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return MOE;
            }
        }
    }
}
