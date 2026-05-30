package block_party.entities.preferences;

import block_party.entities.Moe;
import block_party.scene.SceneTrigger;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class MoeGiftMemory {
    private final Moe moe;
    private int ticks;
    private MoeItemPreferences.PreferenceSignal signal = MoeItemPreferences.PreferenceSignal.neutral();
    private ItemStack item = ItemStack.EMPTY;

    public MoeGiftMemory(Moe moe) {
        this.moe = moe;
    }

    public MoeItemPreferences.PreferenceSignal receive(ItemStack stack) {
        MoeItemPreferences.PreferenceSignal received = MoeItemPreferences.signal(this.moe, stack);
        this.signal = received;
        this.item = stack == null ? ItemStack.EMPTY : stack.copy();
        this.ticks = 20 * 20;
        this.react(received);
        this.moe.triggerScene(SceneTrigger.GIFT_RECEIVED);
        return received;
    }

    public Optional<MoeItemPreferences.PreferenceSignal> latestSignal() {
        return this.ticks > 0 ? Optional.of(this.signal) : Optional.empty();
    }

    public Optional<ItemStack> latestItem() {
        return this.ticks > 0 && !this.item.isEmpty() ? Optional.of(this.item.copy()) : Optional.empty();
    }

    public void tick() {
        if (this.ticks <= 0) {
            return;
        }
        --this.ticks;
        if (this.ticks <= 0) {
            this.signal = MoeItemPreferences.PreferenceSignal.neutral();
            this.item = ItemStack.EMPTY;
        }
    }

    private void react(MoeItemPreferences.PreferenceSignal signal) {
        if (signal.disliked()) {
            this.moe.setEmotion("SNOOTY");
            this.moe.setTemporaryAnimationKey("SHIVER", 50);
            return;
        }
        if (signal.wantsToBeg() || signal.liked()) {
            this.moe.setEmotion("HAPPY");
            this.moe.setTemporaryAnimationKey("HAPPY_DANCE", 60);
            return;
        }
        if (signal.interesting()) {
            this.moe.setEmotion("CONFUSED");
            this.moe.setTemporaryAnimationKey("AWE", 50);
        }
    }
}
