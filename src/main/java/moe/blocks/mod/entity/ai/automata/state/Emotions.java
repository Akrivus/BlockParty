package moe.blocks.mod.entity.ai.automata.state;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.partial.InteractEntity;
import moe.blocks.mod.init.MoeSounds;
import net.minecraft.util.SoundEvent;

import java.util.List;

public enum Emotions {
    ANGRY(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_ANGRY.get(), 100),
    BEGGING(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_BEGGING.get(), 101),
    CONFUSED(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_CONFUSED.get(), 102),
    CRYING(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_CRYING.get(), 103),
    MISCHIEVOUS(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_MISCHIEVOUS.get(), 104),
    EMBARRASSED(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_EMBARRASSED.get(), 105),
    HAPPY(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_HAPPY.get(), 106),
    NORMAL(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_NORMAL.get(), 107),
    PAINED(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_PAINED.get(), 108),
    PSYCHOTIC(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_PSYCHOTIC.get(), 109),
    SCARED(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_SCARED.get(), 110),
    SMITTEN(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_SMITTEN.get(), 111),
    TIRED(new State<InteractEntity>() {
        @Override
        public void apply(List<IStateGoal> goals, InteractEntity entity) {

        }
    }, MoeSounds.MOE_EMOTION_TIRED.get(), 112);

    public final State state;
    public final SoundEvent sound;
    public final int id;

    Emotions(State state, SoundEvent sound, int id) {
        this.state = state;
        this.sound = sound;
        this.id = id;
    }
}
