package block_party.npc.automata.trait;

import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.ITrait;
import block_party.registry.resources.DollSounds;
import block_party.scene.ISceneRequirement;
import net.minecraft.world.entity.player.Player;

public enum Emotion implements ITrait<Emotion> {
    ANGRY(DollSounds.Sound.ANGRY),
    BEGGING(DollSounds.Sound.NEUTRAL),
    CONFUSED(DollSounds.Sound.CONFUSED),
    CRYING(DollSounds.Sound.CRYING),
    MISCHIEVOUS(DollSounds.Sound.SNICKER),
    EMBARRASSED(DollSounds.Sound.ATTACK),
    HAPPY(DollSounds.Sound.GIGGLE),
    NORMAL(DollSounds.Sound.LAUGH),
    PAINED(DollSounds.Sound.ATTACK),
    PSYCHOTIC(DollSounds.Sound.PSYCHOTIC),
    SCARED(DollSounds.Sound.CRYING),
    SICK(DollSounds.Sound.SNEEZE),
    SNOOTY(DollSounds.Sound.SNOOTY),
    SMITTEN(DollSounds.Sound.SMITTEN),
    TIRED(DollSounds.Sound.YAWN);

    private final DollSounds.Sound sound;

    Emotion(DollSounds.Sound sound) {
        this.sound = sound;
    }

    @Override
    public boolean isSharedWith(BlockPartyNPC npc) {
        return npc.getEmotion() == this;
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public Emotion fromValue(String key) {
        try {
            return Emotion.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }
}
