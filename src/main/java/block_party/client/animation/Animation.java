package block_party.client.animation;

import block_party.client.animation.state.DefaultAnimation;
import block_party.client.animation.state.WaveAnimation;
import block_party.client.animation.state.YearbookAnimation;
import block_party.npc.automata.trait.BloodType;
import block_party.scene.SceneTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public enum Animation {
    DEFAULT(DefaultAnimation::new), YEARBOOK(YearbookAnimation::new), WAVE(WaveAnimation::new);

    private final Supplier<AbstractAnimation> animation;

    Animation(Supplier<AbstractAnimation> animation) {
        this.animation = animation;
    }

    public AbstractAnimation fromValue(String key) {
        try {
            return Animation.valueOf(key.toUpperCase()).get();
        } catch (IllegalArgumentException e) {
            return this.get();
        }
    }

    public AbstractAnimation fromValue(ResourceLocation location) {
        return fromValue(location.getPath());
    }

    public AbstractAnimation get() {
        return this.animation.get();
    }

    public Animation fromKey(String key) {
        try {
            return Animation.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }

    public Animation fromKey(ResourceLocation location) {
        return this.fromKey(location.getPath());
    }
}
