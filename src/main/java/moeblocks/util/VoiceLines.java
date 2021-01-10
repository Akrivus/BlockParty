package moeblocks.util;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeSounds;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

public enum VoiceLines {
    ANGRY(MoeSounds.ENTITY_MOE_ANGRY),
    ATTACK(MoeSounds.ENTITY_MOE_ATTACK),
    CONFUSED(MoeSounds.ENTITY_MOE_CONFUSED),
    CRYING(MoeSounds.ENTITY_MOE_CRYING),
    DEAD(MoeSounds.ENTITY_MOE_DEAD),
    EAT(MoeSounds.ENTITY_MOE_EAT),
    EQUIP(MoeSounds.ENTITY_MOE_EQUIP),
    FEED(MoeSounds.ENTITY_MOE_FEED),
    FOLLOW(MoeSounds.ENTITY_MOE_FOLLOW),
    GIGGLE(MoeSounds.ENTITY_MOE_GIGGLE),
    GRIEF(MoeSounds.ENTITY_MOE_GRIEF),
    HAPPY(MoeSounds.ENTITY_MOE_HAPPY),
    HELLO(MoeSounds.ENTITY_MOE_HELLO),
    HURT(MoeSounds.ENTITY_MOE_HURT),
    LAUGH(MoeSounds.ENTITY_MOE_LAUGH),
    MEOW(MoeSounds.ENTITY_MOE_MEOW),
    NEUTRAL(MoeSounds.ENTITY_MOE_NEUTRAL),
    NO(MoeSounds.ENTITY_MOE_NO),
    PSYCHOTIC(MoeSounds.ENTITY_MOE_PSYCHOTIC),
    SENPAI(MoeSounds.ENTITY_MOE_SENPAI),
    SLEEPING(MoeSounds.ENTITY_MOE_SLEEPING),
    SMITTEN(MoeSounds.ENTITY_MOE_SMITTEN),
    SNEEZE(MoeSounds.ENTITY_MOE_SNEEZE),
    SNICKER(MoeSounds.ENTITY_MOE_SNICKER),
    SNOOTY(MoeSounds.ENTITY_MOE_SNOOTY),
    YAWN(MoeSounds.ENTITY_MOE_YAWN),
    YES(MoeSounds.ENTITY_MOE_YES);
    
    private final RegistryObject<SoundEvent> girl;
    
    VoiceLines(RegistryObject<SoundEvent> girl) {
        this.girl = girl;
    }
    
    public SoundEvent get(AbstractNPCEntity entity) {
        return this.girl.get();
    }
}


