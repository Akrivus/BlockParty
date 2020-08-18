package mod.moeblocks.entity.util;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.register.SoundEventsMoe;
import net.minecraft.util.SoundEvent;

public enum VoiceLines {
    ATTACK(SoundEventsMoe.MOE_ATTACK.get(), SoundEventsMoe.SENPAI_ATTACK.get()),
    DEAD(SoundEventsMoe.MOE_DEAD.get(), SoundEventsMoe.SENPAI_DEAD.get()),
    EAT(SoundEventsMoe.MOE_EAT.get(), SoundEventsMoe.SENPAI_EAT.get()),
    EMOTION_ANGRY(SoundEventsMoe.MOE_EMOTION_ANGRY.get(), SoundEventsMoe.SENPAI_EMOTION_ANGRY.get()),
    EMOTION_BEGGING(SoundEventsMoe.MOE_EMOTION_BEGGING.get(), SoundEventsMoe.SENPAI_EMOTION_BEGGING.get()),
    EMOTION_CONFUSED(SoundEventsMoe.MOE_EMOTION_CONFUSED.get(), SoundEventsMoe.SENPAI_EMOTION_CONFUSED.get()),
    EMOTION_CRYING(SoundEventsMoe.MOE_EMOTION_CRYING.get(), SoundEventsMoe.SENPAI_EMOTION_CRYING.get()),
    EMOTION_EMBARRASSED(SoundEventsMoe.MOE_EMOTION_EMBARRASSED.get(), SoundEventsMoe.SENPAI_EMOTION_EMBARRASSED.get()),
    EMOTION_HAPPY(SoundEventsMoe.MOE_EMOTION_HAPPY.get(), SoundEventsMoe.SENPAI_EMOTION_HAPPY.get()),
    EMOTION_MISCHIEVOUS(SoundEventsMoe.MOE_EMOTION_MISCHIEVOUS.get(), SoundEventsMoe.SENPAI_EMOTION_MISCHIEVOUS.get()),
    EMOTION_NORMAL(SoundEventsMoe.MOE_EMOTION_NORMAL.get(), SoundEventsMoe.SENPAI_EMOTION_NORMAL.get()),
    EMOTION_PAINED(SoundEventsMoe.MOE_EMOTION_PAINED.get(), SoundEventsMoe.SENPAI_EMOTION_PAINED.get()),
    EMOTION_PSYCHOTIC(SoundEventsMoe.MOE_EMOTION_PSYCHOTIC.get(), SoundEventsMoe.SENPAI_EMOTION_PSYCHOTIC.get()),
    EMOTION_SCARED(SoundEventsMoe.MOE_EMOTION_SCARED.get(), SoundEventsMoe.SENPAI_EMOTION_SCARED.get()),
    EMOTION_SMITTEN(SoundEventsMoe.MOE_EMOTION_SMITTEN.get(), SoundEventsMoe.SENPAI_EMOTION_SMITTEN.get()),
    EMOTION_TIRED(SoundEventsMoe.MOE_EMOTION_TIRED.get(), SoundEventsMoe.SENPAI_EMOTION_TIRED.get()),
    GREET_LEVEL_1(SoundEventsMoe.MOE_GREET_LEVEL_1.get(), SoundEventsMoe.SENPAI_GREET_LEVEL_1.get()),
    GREET_LEVEL_2(SoundEventsMoe.MOE_GREET_LEVEL_2.get(), SoundEventsMoe.SENPAI_GREET_LEVEL_2.get()),
    GREET_LEVEL_3(SoundEventsMoe.MOE_GREET_LEVEL_3.get(), SoundEventsMoe.SENPAI_GREET_LEVEL_3.get()),
    GREET_LEVEL_4(SoundEventsMoe.MOE_GREET_LEVEL_4.get(), SoundEventsMoe.SENPAI_GREET_LEVEL_4.get()),
    HURT(SoundEventsMoe.MOE_HURT.get(), SoundEventsMoe.SENPAI_HURT.get()),
    NO(SoundEventsMoe.MOE_NO.get(), SoundEventsMoe.SENPAI_NO.get()),
    SING(SoundEventsMoe.MOE_SING.get(), SoundEventsMoe.SENPAI_SING.get()),
    THANK_YOU(SoundEventsMoe.MOE_THANK_YOU.get(), SoundEventsMoe.SENPAI_THANK_YOU.get()),
    YES(SoundEventsMoe.MOE_YES.get(), SoundEventsMoe.SENPAI_YES.get());

    private final SoundEvent moe;
    private final SoundEvent senpai;

    VoiceLines(SoundEvent moe, SoundEvent senpai) {
        this.moe = moe;
        this.senpai = moe;
    }

    public SoundEvent get(StateEntity entity) {
        return entity instanceof MoeEntity ? this.moe : this.senpai;
    }
}
