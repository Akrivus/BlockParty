package moeblocks.util;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeSounds;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

public enum VoiceLines {
    ANGRY(MoeSounds.MOE_ENTITY_ANGRY),
    ATTACK(MoeSounds.MOE_ENTITY_ATTACK),
    CONFUSED(MoeSounds.MOE_ENTITY_CONFUSED),
    CRYING(MoeSounds.MOE_ENTITY_CRYING),
    DEAD(MoeSounds.MOE_ENTITY_DEAD),
    EAT(MoeSounds.MOE_ENTITY_EAT),
    EQUIP(MoeSounds.MOE_ENTITY_EQUIP),
    FEED(MoeSounds.MOE_ENTITY_FEED),
    FOLLOW(MoeSounds.MOE_ENTITY_FOLLOW),
    GIGGLE(MoeSounds.MOE_ENTITY_GIGGLE),
    GRIEF(MoeSounds.MOE_ENTITY_GRIEF),
    HAPPY(MoeSounds.MOE_ENTITY_HAPPY),
    HELLO(MoeSounds.MOE_ENTITY_HELLO),
    HURT(MoeSounds.MOE_ENTITY_HURT),
    LAUGH(MoeSounds.MOE_ENTITY_LAUGH),
    MEOW(MoeSounds.MOE_ENTITY_MEOW),
    NEUTRAL(MoeSounds.MOE_ENTITY_NEUTRAL),
    NO(MoeSounds.MOE_ENTITY_NO),
    PSYCHOTIC(MoeSounds.MOE_ENTITY_PSYCHOTIC),
    SENPAI(MoeSounds.MOE_ENTITY_SENPAI),
    SLEEPING(MoeSounds.MOE_ENTITY_SLEEPING),
    SMITTEN(MoeSounds.MOE_ENTITY_SMITTEN),
    SNEEZE(MoeSounds.MOE_ENTITY_SNEEZE),
    SNICKER(MoeSounds.MOE_ENTITY_SNICKER),
    SNOOTY(MoeSounds.MOE_ENTITY_SNOOTY),
    YAWN(MoeSounds.MOE_ENTITY_YAWN),
    YES(MoeSounds.MOE_ENTITY_YES);
    
    private final RegistryObject<SoundEvent> girl;
    
    VoiceLines(RegistryObject<SoundEvent> girl) {
        this.girl = girl;
    }
    
    public SoundEvent get(AbstractNPCEntity entity) {
        return this.girl.get();
    }
}


