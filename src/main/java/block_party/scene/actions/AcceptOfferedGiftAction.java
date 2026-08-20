package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Transfers the main-hand item that began the current gift conversation. */
public enum AcceptOfferedGiftAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        accept(moe, SceneActionPlayers.targetPlayer(moe));
    }

    public static boolean accept(Moe moe, Player player) {
        ItemStack offered = moe.offeredGift().orElse(ItemStack.EMPTY);
        if (player == null || offered.isEmpty() || !moe.offeredGiftBelongsTo(player)) {
            return false;
        }
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || !ItemStack.isSameItemSameComponents(held, offered)
                || SceneItemStacks.freeSpace(moe.getInventory(), offered) < 1) {
            return false;
        }
        ItemStack accepted = held.copyWithCount(1);
        if (SceneItemStacks.insert(moe.getInventory(), accepted) != 1) {
            return false;
        }
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        moe.clearOfferedGift();
        moe.receiveGift(accepted);
        return true;
    }
}
