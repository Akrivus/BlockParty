package moe.blocks.mod.entity.ai;

import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.entity.SenpaiEntity;
import moe.blocks.mod.init.MoeSounds;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

public enum VoiceLines {
    ANGRY(MoeSounds.MOE_GIRL_ANGRY, MoeSounds.MOE_BOY_ANGRY, MoeSounds.SENPAI_ANGRY),
    ATTACK(MoeSounds.MOE_GIRL_ATTACK, MoeSounds.MOE_BOY_ATTACK, MoeSounds.SENPAI_ATTACK),
    CONFUSED(MoeSounds.MOE_GIRL_CONFUSED, MoeSounds.MOE_BOY_CONFUSED, MoeSounds.SENPAI_CONFUSED),
    CRYING(MoeSounds.MOE_GIRL_CRYING, MoeSounds.MOE_BOY_CRYING, MoeSounds.SENPAI_CRYING),
    DEAD(MoeSounds.MOE_GIRL_DEAD, MoeSounds.MOE_BOY_DEAD, MoeSounds.SENPAI_DEAD),
    EAT(MoeSounds.MOE_GIRL_EAT, MoeSounds.MOE_BOY_EAT, MoeSounds.SENPAI_EAT),
    EQUIP(MoeSounds.MOE_GIRL_EQUIP, MoeSounds.MOE_BOY_EQUIP, MoeSounds.SENPAI_EQUIP),
    FEED(MoeSounds.MOE_GIRL_FEED, MoeSounds.MOE_BOY_FEED, MoeSounds.SENPAI_FEED),
    FOLLOW(MoeSounds.MOE_GIRL_FOLLOW, MoeSounds.MOE_BOY_FOLLOW, MoeSounds.SENPAI_FOLLOW),
    GIGGLE(MoeSounds.MOE_GIRL_GIGGLE, MoeSounds.MOE_BOY_GIGGLE, MoeSounds.SENPAI_GIGGLE),
    GRIEF(MoeSounds.MOE_GIRL_GRIEF, MoeSounds.MOE_BOY_GRIEF, MoeSounds.SENPAI_GRIEF),
    HAPPY(MoeSounds.MOE_GIRL_HAPPY, MoeSounds.MOE_BOY_HAPPY, MoeSounds.SENPAI_HAPPY),
    HELLO(MoeSounds.MOE_GIRL_HELLO, MoeSounds.MOE_BOY_HELLO, MoeSounds.SENPAI_HELLO),
    HURT(MoeSounds.MOE_GIRL_HURT, MoeSounds.MOE_BOY_HURT, MoeSounds.SENPAI_HURT),
    LAUGH(MoeSounds.MOE_GIRL_LAUGH, MoeSounds.MOE_BOY_LAUGH, MoeSounds.SENPAI_LAUGH),
    MEOW(MoeSounds.MOE_GIRL_MEOW, MoeSounds.MOE_BOY_MEOW, MoeSounds.SENPAI_MEOW),
    NEUTRAL(MoeSounds.MOE_GIRL_NEUTRAL, MoeSounds.MOE_BOY_NEUTRAL, MoeSounds.SENPAI_NEUTRAL),
    NO(MoeSounds.MOE_GIRL_NO, MoeSounds.MOE_BOY_NO, MoeSounds.SENPAI_NO),
    PSYCHOTIC(MoeSounds.MOE_GIRL_PSYCHOTIC, MoeSounds.MOE_BOY_PSYCHOTIC, MoeSounds.SENPAI_PSYCHOTIC),
    SENPAI(MoeSounds.MOE_GIRL_SENPAI, MoeSounds.MOE_BOY_SENPAI, MoeSounds.SENPAI_SENPAI),
    SLEEPING(MoeSounds.MOE_GIRL_SLEEPING, MoeSounds.MOE_BOY_SLEEPING, MoeSounds.SENPAI_SLEEPING),
    SMITTEN(MoeSounds.MOE_GIRL_SMITTEN, MoeSounds.MOE_BOY_SMITTEN, MoeSounds.SENPAI_SMITTEN),
    SNEEZE(MoeSounds.MOE_GIRL_SNEEZE, MoeSounds.MOE_BOY_SNEEZE, MoeSounds.SENPAI_SNEEZE),
    SNICKER(MoeSounds.MOE_GIRL_SNICKER, MoeSounds.MOE_BOY_SNICKER, MoeSounds.SENPAI_SNICKER),
    SNOOTY(MoeSounds.MOE_GIRL_SNOOTY, MoeSounds.MOE_BOY_SNOOTY, MoeSounds.SENPAI_SNOOTY),
    YAWN(MoeSounds.MOE_GIRL_YAWN, MoeSounds.MOE_BOY_YAWN, MoeSounds.SENPAI_YAWN),
    YES(MoeSounds.MOE_GIRL_YES, MoeSounds.MOE_BOY_YES, MoeSounds.SENPAI_YES);

    private final RegistryObject<SoundEvent> girl;
    private final RegistryObject<SoundEvent> boy;
    private final RegistryObject<SoundEvent> male;

    VoiceLines(RegistryObject<SoundEvent> girl, RegistryObject<SoundEvent> boy, RegistryObject<SoundEvent> male) {
        this.girl = girl;
        this.boy = boy;
        this.male = male;
    }

    public SoundEvent get(AbstractNPCEntity entity) {
        if (entity instanceof SenpaiEntity) { return male.get(); }
        if (entity.getGender() == AbstractNPCEntity.Genders.FEMININE) {
            return this.girl.get();
        }
        return this.boy.get();
    }
}


