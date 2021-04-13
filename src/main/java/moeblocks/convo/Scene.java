package moeblocks.convo;

import moeblocks.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeMessages;
import moeblocks.message.SCloseDialogue;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Scene {
    private final BiConsumer<PlayerEntity, AbstractNPCEntity> action;
    private final Map<Response, Transition> transitions = new HashMap<>();
    private PlayerEntity player;
    private AbstractNPCEntity npc;

    public Scene(BiConsumer<PlayerEntity, AbstractNPCEntity> action, Transition... transitions) {
        this.action = action;
        for (Transition transition : transitions) {
            if (this.transitions.get(transition.getResponse()) != null) {
                throw new IllegalArgumentException("Scene contains multiple identical response types.");
            }
            this.transitions.put(transition.getResponse(), transition);
        }
    }

    public void act(PlayerEntity player, AbstractNPCEntity npc) {
        this.action.accept(this.player = player, this.npc = npc);
    }

    public Scene next(Response response) {
        switch (response) {
        //case CONVO:
        //    return this.npc.findResponseTo(Interaction.CONVO);
        case BLOCK:
            this.npc.openSpecialMenuFor(this.player);
            return this.next(Response.CLOSE);
        case CHEST:
            this.npc.openChestFor(this.player);
            return this.next(Response.CLOSE);
        case CLOSE:
            MoeMessages.send(this.player, new SCloseDialogue());
        default:
            Transition transition = this.transitions.get(response);
            if (transition == null) { return null; }
            return transition.getScene();
        }
    }
}