package block_party.npc.automata.trait;

import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.IState;
import block_party.npc.automata.ITrait;

public enum Emotion implements ITrait<Emotion> {
    ANGRY, BEGGING, CONFUSED, CRYING, MISCHIEVOUS, EMBARRASSED, HAPPY, NORMAL, PAINED, PSYCHOTIC, SCARED, SICK, SNOOTY, SMITTEN, TIRED;

    Emotion() {

    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public Emotion fromValue(String key) {
        try {
            return Emotion.valueOf(key);
        } catch (IllegalArgumentException e) {
            return Emotion.NORMAL;
        }
    }

    @Override
    public boolean isTrue(BlockPartyNPC entity) {
        return entity.getEmotion() == this;
    }

    @Override
    public IState getStemState() {
        return null;
    }
}