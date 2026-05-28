package block_party.client.animation;

import block_party.client.animation.state.DefaultAnimation;
import block_party.client.animation.state.AweAnimation;
import block_party.client.animation.state.BegAnimation;
import block_party.client.animation.state.HappyDanceAnimation;
import block_party.client.animation.state.LookAroundAnimation;
import block_party.client.animation.state.ShiverAnimation;
import block_party.client.animation.state.WaveAnimation;
import block_party.client.animation.state.YearbookAnimation;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public enum Animation {
    DEFAULT(DefaultAnimation::new),
    AWE(AweAnimation::new),
    BEG(BegAnimation::new),
    HAPPY_DANCE(HappyDanceAnimation::new),
    LOOK_AROUND(LookAroundAnimation::new),
    SHIVER(ShiverAnimation::new),
    YEARBOOK(YearbookAnimation::new),
    WAVE(WaveAnimation::new);

    private final Supplier<AbstractAnimation> animation;

    Animation(Supplier<AbstractAnimation> animation) {
        this.animation = animation;
    }

    public AbstractAnimation fromValue(ResourceLocation location) {
        return fromValue(location.getPath());
    }

    public AbstractAnimation fromValue(String key) {
        return fromKey(key).get();
    }

    public AbstractAnimation get() {
        return this.animation.get();
    }

    public Animation fromKey(ResourceLocation location) {
        return this.fromKey(location.getPath());
    }

    public Animation fromKey(String key) {
        try {
            return Animation.valueOf(key.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException exception) {
            return this;
        }
    }
}
