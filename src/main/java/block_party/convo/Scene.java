package block_party.convo;

import block_party.convo.enums.Response;
import block_party.init.BlockPartyMessages;
import block_party.message.SCloseDialogue;
import block_party.mob.Partyer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Scene {
    private final BiConsumer<Player, Partyer> action;
    private final Map<Response, Transition> transitions = new HashMap<>();
    private Player player;
    private Partyer npc;

    public Scene(BiConsumer<Player, Partyer> action, Transition... transitions) {
        this.action = action;
        for (Transition transition : transitions) {
            if (this.transitions.get(transition.getResponse()) != null) {
                throw new IllegalArgumentException("Scene contains multiple identical response types.");
            }
            this.transitions.put(transition.getResponse(), transition);
        }
    }

    public void act(Player player, Partyer npc) {
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
            BlockPartyMessages.send(this.player, new SCloseDialogue());
        default:
            Transition transition = this.transitions.get(response);
            if (transition == null) { return null; }
            return transition.getScene();
        }
    }
}
