package moeblocks.automata.trait;

import moeblocks.automata.IState;
import moeblocks.automata.ITrait;
import moeblocks.entity.AbstractNPCEntity;

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
    public boolean isTrue(AbstractNPCEntity entity) {
        return entity.getEmotion() == this;
    }

    @Override
    public IState getStemState() {
        return null;
    }
}
