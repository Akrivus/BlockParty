package moeblocks.data.conversation;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.ai.automata.state.Emotions;
import moeblocks.init.MoeDialogues;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class Dialogue extends ForgeRegistryEntry<Dialogue> {
    private String tag;
    private String[] conditions;
    private String message;
    private String emotion;
    private String reaction;
    private String trigger;
    private String[] responses;

    public boolean matches(Triggers trigger, AbstractNPCEntity character, PlayerEntity player) {
        if (!trigger.equals(this.trigger)) { return false; }
        return this.matches(character, player);
    }

    public boolean matches(AbstractNPCEntity character, PlayerEntity player) {
        Conditions[] conditions = this.getConditions();
        for (int i = 0; i < conditions.length; ++i) {
            if (!conditions[i].test(character, player)) { return false; }
        }
        return true;
    }

    public Conditions[] getConditions() {
        Conditions[] temp = new Conditions[this.conditions.length];
        for (int i = 0; i < temp.length; ++i) {
            temp[i] = Conditions.valueOf(this.conditions[i].toUpperCase());
        }
        return temp;
    }

    public Emotions getEmotion() {
        return Emotions.valueOf(this.emotion.toUpperCase());
    }

    public String getKey() {
        return this.tag;
    }

    public String getMessage() {
        return this.message;
    }

    public Reactions getReactions() {
        return Reactions.valueOf(this.reaction.toUpperCase());
    }

    public List<Dialogue> getResponses() {
        List<Dialogue> temp = new ArrayList<>();
        for (int i = 0; i < this.responses.length; ++i) {
            temp.add(MoeDialogues.REGISTRY.get(this.responses[i]));
        }
        return temp;
    }

    public Triggers getTriggers() {
        return Triggers.valueOf(this.trigger.toUpperCase());
    }
}
