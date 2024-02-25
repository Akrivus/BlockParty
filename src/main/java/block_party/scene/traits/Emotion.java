package block_party.scene.traits;

import block_party.entities.BlockPartyNPC;
import block_party.registry.resources.MoeSounds;
import block_party.scene.ITrait;

public enum Emotion implements ITrait<Emotion> {
    ANGRY(MoeSounds.Sound.ANGRY), BEGGING(MoeSounds.Sound.NEUTRAL), CONFUSED(MoeSounds.Sound.CONFUSED), CRYING(MoeSounds.Sound.CRYING), MISCHIEVOUS(MoeSounds.Sound.SNICKER), EMBARRASSED(MoeSounds.Sound.ATTACK), HAPPY(MoeSounds.Sound.GIGGLE), NORMAL(MoeSounds.Sound.LAUGH), PAINED(MoeSounds.Sound.ATTACK), PSYCHOTIC(MoeSounds.Sound.PSYCHOTIC), SCARED(MoeSounds.Sound.CRYING), SICK(MoeSounds.Sound.SNEEZE), SNOOTY(MoeSounds.Sound.SNOOTY), SMITTEN(MoeSounds.Sound.SMITTEN), TIRED(MoeSounds.Sound.YAWN);

    private final MoeSounds.Sound sound;

    Emotion(MoeSounds.Sound sound) {
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
