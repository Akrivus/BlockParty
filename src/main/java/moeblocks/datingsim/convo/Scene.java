package moeblocks.datingsim.convo;

import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;
import java.util.function.BiConsumer;

public class Scene {
    private static int ID = 0;
    private final BiConsumer<PlayerEntity, AbstractNPCEntity> action;
    private final Transition[] transitions;
    private final int id;
    
    public Scene(BiConsumer<PlayerEntity, AbstractNPCEntity> action, Transition... transitions) {
        this.action = action;
        this.transitions = transitions;
        this.id = ++Scene.ID;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    public void act(PlayerEntity player, AbstractNPCEntity npc) {
        this.action.accept(player, npc);
    }
    
    public Scene next(Response response) {
        for (Transition transition : this.transitions) {
            if (transition.getResponse() == response) { return transition.getScene(); }
        }
        return null;
    }
}
