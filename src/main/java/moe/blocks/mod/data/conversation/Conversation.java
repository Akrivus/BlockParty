package moe.blocks.mod.data.conversation;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.data.dating.Tropes;
import moe.blocks.mod.entity.ai.automata.state.Emotions;

public class Conversation {
    public String condition;
    public String[] tropes;
    public Dialogue dialogue;

    public boolean isTrope(Tropes trope) {
        if (this.tropes == null) { return true; }
        for (String name : this.tropes) {
            if (trope == Tropes.valueOf(name.toUpperCase())) { return true; }
        }
        return false;
    }

    public Conditions getCondition() {
        if (this.condition == null) { return Conditions.ALWAYS; }
        return Conditions.valueOf(this.condition.toUpperCase());
    }

    public static class Dialogue {
        public String animation;
        public String line;
        public Response[] responses;

        public Animations getAnimation() {
            if (this.animation == null) { return Animations.IDLE; }
            return Animations.valueOf(this.animation.toUpperCase());
        }
    }

    public static class Response {
        public String emotion;
        public String reaction;
        public Reward reward;
        public Dialogue dialogue;

        public Emotions getEmotion() {
            if (this.emotion == null) { return Emotions.NORMAL; }
            return Emotions.valueOf(this.emotion.toUpperCase());
        }

        public Reactions getReaction() {
            if (this.reaction == null) { return Reactions.NONE; }
            return Reactions.valueOf(this.reaction.toUpperCase());
        }
    }

    public static class Reward {
        public boolean dropHeldItem;
        public float love;
        public float trust;
        public float stress;
    }
}
