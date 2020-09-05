package moe.blocks.mod.entity.util;

import moe.blocks.mod.entity.emotion.*;

import java.util.Arrays;
import java.util.function.Supplier;

public enum Emotions {
    ANGRY(AngryEmotion::new), BEGGING(BeggingEmotion::new), CONFUSED(ConfusedEmotion::new), CRYING(CryingEmotion::new), DEAD(AbstractEmotion::new), MISCHIEVOUS(MischievousEmotion::new), EMBARRASSED(EmbarrassedEmotion::new), HAPPY(HappyEmotion::new), NORMAL(NormalEmotion::new), PAINED(PainedEmotion::new), PSYCHOTIC(PsychoticEmotion::new), SCARED(ScaredEmotion::new), SMITTEN(SmittenEmotion::new), TIRED(TiredEmotion::new);

    private final Supplier<? extends AbstractEmotion> emotion;

    Emotions(Supplier<? extends AbstractEmotion> emotion) {
        this.emotion = emotion;
    }

    public AbstractEmotion get() {
        return this.emotion.get();
    }

    public boolean matches(Enum<?>... emotions) {
        return Arrays.stream(emotions).anyMatch(emotion -> this == emotion);
    }
}
