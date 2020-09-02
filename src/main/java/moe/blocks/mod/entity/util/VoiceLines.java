package moe.blocks.mod.entity.util;

import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.StudentEntity;
import moe.blocks.mod.init.MoeSounds;
import net.minecraft.util.SoundEvent;

public enum VoiceLines {
    ATTACK(MoeSounds.MOE_ATTACK.get(), MoeSounds.SENPAI_ATTACK.get()),
    DEAD(MoeSounds.MOE_DEAD.get(), MoeSounds.SENPAI_DEAD.get()),
    EAT(MoeSounds.MOE_EAT.get(), MoeSounds.SENPAI_EAT.get()),
    EMOTION_ANGRY(MoeSounds.MOE_EMOTION_ANGRY.get(), MoeSounds.SENPAI_EMOTION_ANGRY.get()),
    EMOTION_BEGGING(MoeSounds.MOE_EMOTION_BEGGING.get(), MoeSounds.SENPAI_EMOTION_BEGGING.get()),
    EMOTION_CONFUSED(MoeSounds.MOE_EMOTION_CONFUSED.get(), MoeSounds.SENPAI_EMOTION_CONFUSED.get()),
    EMOTION_CRYING(MoeSounds.MOE_EMOTION_CRYING.get(), MoeSounds.SENPAI_EMOTION_CRYING.get()),
    EMOTION_EMBARRASSED(MoeSounds.MOE_EMOTION_EMBARRASSED.get(), MoeSounds.SENPAI_EMOTION_EMBARRASSED.get()),
    EMOTION_HAPPY(MoeSounds.MOE_EMOTION_HAPPY.get(), MoeSounds.SENPAI_EMOTION_HAPPY.get()),
    EMOTION_MISCHIEVOUS(MoeSounds.MOE_EMOTION_MISCHIEVOUS.get(), MoeSounds.SENPAI_EMOTION_MISCHIEVOUS.get()),
    EMOTION_NORMAL(MoeSounds.MOE_EMOTION_NORMAL.get(), MoeSounds.SENPAI_EMOTION_NORMAL.get()),
    EMOTION_PAINED(MoeSounds.MOE_EMOTION_PAINED.get(), MoeSounds.SENPAI_EMOTION_PAINED.get()),
    EMOTION_PSYCHOTIC(MoeSounds.MOE_EMOTION_PSYCHOTIC.get(), MoeSounds.SENPAI_EMOTION_PSYCHOTIC.get()),
    EMOTION_SCARED(MoeSounds.MOE_EMOTION_SCARED.get(), MoeSounds.SENPAI_EMOTION_SCARED.get()),
    EMOTION_SMITTEN(MoeSounds.MOE_EMOTION_SMITTEN.get(), MoeSounds.SENPAI_EMOTION_SMITTEN.get()),
    EMOTION_TIRED(MoeSounds.MOE_EMOTION_TIRED.get(), MoeSounds.SENPAI_EMOTION_TIRED.get()),
    GREET_LEVEL_1(MoeSounds.MOE_GREET_LEVEL_1.get(), MoeSounds.SENPAI_GREET_LEVEL_1.get()),
    GREET_LEVEL_2(MoeSounds.MOE_GREET_LEVEL_2.get(), MoeSounds.SENPAI_GREET_LEVEL_2.get()),
    GREET_LEVEL_3(MoeSounds.MOE_GREET_LEVEL_3.get(), MoeSounds.SENPAI_GREET_LEVEL_3.get()),
    GREET_LEVEL_4(MoeSounds.MOE_GREET_LEVEL_4.get(), MoeSounds.SENPAI_GREET_LEVEL_4.get()),
    HURT(MoeSounds.MOE_HURT.get(), MoeSounds.SENPAI_HURT.get()),
    NO(MoeSounds.MOE_NO.get(), MoeSounds.SENPAI_NO.get()),
    SING(MoeSounds.MOE_SING.get(), MoeSounds.SENPAI_SING.get()),
    THANK_YOU(MoeSounds.MOE_THANK_YOU.get(), MoeSounds.SENPAI_THANK_YOU.get()),
    YES(MoeSounds.MOE_YES.get(), MoeSounds.SENPAI_YES.get());

    private final SoundEvent moe;
    private final SoundEvent senpai;

    VoiceLines(SoundEvent moe, SoundEvent senpai) {
        this.moe = moe;
        this.senpai = moe;
    }

    public SoundEvent get(StudentEntity entity) {
        return entity instanceof MoeEntity ? this.moe : this.senpai;
    }
}
