package block_party.scene.traits;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ITrait;

public enum Emotion implements ITrait<Emotion> {
    ANGRY, BEGGING, CONFUSED, CRYING, MISCHIEVOUS, EMBARRASSED, HAPPY, NORMAL, PAINED, PSYCHOTIC, SCARED, SICK, SNOOTY, SMITTEN, TIRED;

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
