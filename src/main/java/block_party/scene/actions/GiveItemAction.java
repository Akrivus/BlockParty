package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record GiveItemAction(ItemStack stack, Target target) implements SceneAction {
    public GiveItemAction {
        stack = stack == null ? ItemStack.EMPTY : stack.copy();
        target = target == null ? Target.PLAYER : target;
    }

    @Override
    public void apply(Moe moe) {
        if (this.stack.isEmpty()) {
            return;
        }
        ItemStack gift = this.stack.copy();
        if (this.target == Target.MOE) {
            SceneItemStacks.insert(moe.getInventory(), gift);
            return;
        }
        Player player = SceneActionPlayers.targetPlayer(moe);
        if (player != null) {
            player.getInventory().add(gift);
        }
    }

    public enum Target {
        PLAYER,
        MOE;

        public static Target fromValue(String value) {
            try {
                return Target.valueOf(value.toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return PLAYER;
            }
        }
    }
}
