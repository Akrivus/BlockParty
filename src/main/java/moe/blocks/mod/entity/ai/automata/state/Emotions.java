package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.entity.ai.VoiceLines;
import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.init.MoeSounds;
import net.minecraft.util.SoundEvent;

import java.util.List;

public enum Emotions {
    ANGRY(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.ANGRY, 100),
    BEGGING(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.SMITTEN, 101),
    CONFUSED(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.CONFUSED, 102),
    CRYING(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.CRYING, 103),
    MISCHIEVOUS(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.GIGGLE, 104),
    EMBARRASSED(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.GRIEF, 105),
    HAPPY(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.HAPPY, 106),
    NORMAL(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.NEUTRAL, 107),
    PAINED(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.CONFUSED, 108),
    PSYCHOTIC(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.PSYCHOTIC, 109),
    SCARED(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.GRIEF, 110),
    SNOOTY(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.SNOOTY, 111),
    SMITTEN(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.SMITTEN, 112),
    TIRED(new State<AbstractNPCEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {

        }
    }, VoiceLines.YAWN, 113);

    private final VoiceLines line;
    private final byte particle;
    public final State state;

    Emotions(State state, VoiceLines line, int id) {
        this.state = state;
        this.line = line;
        this.particle = (byte) id;
    }

    public SoundEvent getSound(AbstractNPCEntity entity) {
        return this.line.get(entity);
    }

    public byte getParticle() {
        return this.particle;
    }
}
