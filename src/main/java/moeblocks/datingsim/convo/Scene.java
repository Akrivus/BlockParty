package moeblocks.datingsim.convo;

import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiConsumer;

public class Scene {
    private final BiConsumer<PlayerEntity, AbstractNPCEntity> action;
    private final Transition[] transitions;
    
    public Scene(BiConsumer<PlayerEntity, AbstractNPCEntity> action, Transition... transitions) {
        this.action = action;
        this.transitions = transitions;
    }
    
    public void act(PlayerEntity player, AbstractNPCEntity npc) {
        this.action.accept(player, npc);
    }
    
    public Scene next(AbstractNPCEntity npc, Response response) {
        for (Transition transition : this.transitions) {
            if (transition.getResponse() == response) { return transition.getScene(npc); }
        }
        return null;
    }
}
