package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.world.progression.PlayerProgressionCounters;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

/** Consumes a tagged portion of the target player's progression ledger. */
public record ResetProgressionCountersAction(Kind kind, ResourceLocation id) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        if (!(moe.level() instanceof ServerLevel level) || this.id == null) {
            return;
        }
        var player = SceneActionPlayers.targetPlayerUuid(moe);
        if (this.kind == Kind.BLOCK) {
            PlayerProgressionCounters.resetBlock(level, player, BuiltInRegistries.BLOCK.getValue(this.id));
        } else {
            PlayerProgressionCounters.resetItem(level, player, BuiltInRegistries.ITEM.getValue(this.id));
        }
    }

    public enum Kind {
        ITEM,
        BLOCK;

        public static Kind fromValue(String value) {
            return "block".equalsIgnoreCase(value) ? BLOCK : ITEM;
        }
    }
}
