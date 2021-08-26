package block_party.util;

import block_party.init.BlockPartySounds;
import block_party.mob.Partyer;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;

public enum VoiceLines {
    ANGRY(BlockPartySounds.ENTITY_MOE_ANGRY), ATTACK(BlockPartySounds.ENTITY_MOE_ATTACK), CONFUSED(BlockPartySounds.ENTITY_MOE_CONFUSED), CRYING(BlockPartySounds.ENTITY_MOE_CRYING), DEAD(BlockPartySounds.ENTITY_MOE_DEAD), EAT(BlockPartySounds.ENTITY_MOE_EAT), EQUIP(BlockPartySounds.ENTITY_MOE_EQUIP), FEED(BlockPartySounds.ENTITY_MOE_FEED), FOLLOW(BlockPartySounds.ENTITY_MOE_FOLLOW), GIGGLE(BlockPartySounds.ENTITY_MOE_GIGGLE), GRIEF(BlockPartySounds.ENTITY_MOE_GRIEF), HAPPY(BlockPartySounds.ENTITY_MOE_HAPPY), HELLO(BlockPartySounds.ENTITY_MOE_HELLO), HURT(BlockPartySounds.ENTITY_MOE_HURT), LAUGH(BlockPartySounds.ENTITY_MOE_LAUGH), MEOW(BlockPartySounds.ENTITY_MOE_MEOW), NEUTRAL(BlockPartySounds.ENTITY_MOE_NEUTRAL), NO(BlockPartySounds.ENTITY_MOE_NO), PSYCHOTIC(BlockPartySounds.ENTITY_MOE_PSYCHOTIC), SENPAI(BlockPartySounds.ENTITY_MOE_SENPAI), SLEEPING(BlockPartySounds.ENTITY_MOE_SLEEPING), SMITTEN(BlockPartySounds.ENTITY_MOE_SMITTEN), SNEEZE(BlockPartySounds.ENTITY_MOE_SNEEZE), SNICKER(BlockPartySounds.ENTITY_MOE_SNICKER), SNOOTY(BlockPartySounds.ENTITY_MOE_SNOOTY), YAWN(BlockPartySounds.ENTITY_MOE_YAWN), YES(BlockPartySounds.ENTITY_MOE_YES);

    private final RegistryObject<SoundEvent> girl;

    VoiceLines(RegistryObject<SoundEvent> girl) {
        this.girl = girl;
    }

    public SoundEvent get(Partyer entity) {
        return this.girl.get();
    }
}


