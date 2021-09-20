package block_party.npc;

import block_party.custom.CustomSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;

public enum VoiceLines {
    ANGRY(CustomSounds.NPC_ANGRY), ATTACK(CustomSounds.NPC_ATTACK), CONFUSED(CustomSounds.NPC_CONFUSED), CRYING(CustomSounds.NPC_CRYING), DEAD(CustomSounds.NPC_DEAD), EAT(CustomSounds.NPC_EAT), EQUIP(CustomSounds.NPC_EQUIP), FEED(CustomSounds.NPC_FEED), FOLLOW(CustomSounds.NPC_FOLLOW), GIGGLE(CustomSounds.NPC_GIGGLE), GRIEF(CustomSounds.NPC_GRIEF), HAPPY(CustomSounds.NPC_HAPPY), HELLO(CustomSounds.NPC_HELLO), HURT(CustomSounds.NPC_HURT), LAUGH(CustomSounds.NPC_LAUGH), MEOW(CustomSounds.NPC_MEOW), NEUTRAL(CustomSounds.NPC_NEUTRAL), NO(CustomSounds.NPC_NO), PSYCHOTIC(CustomSounds.NPC_PSYCHOTIC), SENPAI(CustomSounds.NPC_SENPAI), SLEEPING(CustomSounds.NPC_SLEEPING), SMITTEN(CustomSounds.NPC_SMITTEN), SNEEZE(CustomSounds.NPC_SNEEZE), SNICKER(CustomSounds.NPC_SNICKER), SNOOTY(CustomSounds.NPC_SNOOTY), YAWN(CustomSounds.NPC_YAWN), YES(CustomSounds.NPC_YES);

    private final RegistryObject<SoundEvent> female;

    VoiceLines(RegistryObject<SoundEvent> female) {
        this.female = female;
    }

    public SoundEvent get(BlockPartyNPC entity) {
        return this.female.get();
    }
}
