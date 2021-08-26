package block_party.client.screen;

import block_party.db.records.NPC;
import block_party.init.BlockPartyMessages;
import block_party.message.CNPCRequest;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public abstract class ControllerScreen<M extends NPC> extends AbstractScreen {
    protected List<UUID> npcs;
    protected M npc;
    protected int index;
    protected int count;

    protected ControllerScreen(List<UUID> npcs, UUID id, int x, int y) {
        super(x, y);
        this.npcs = npcs;
        this.index = this.npcs.indexOf(id);
        this.count = this.npcs.size();
        if (this.index < 0) {
            this.index = 0;
        }
    }

    public Player getPlayer() {
        return this.minecraft.player;
    }

    public void getNPC(int index) {
        this.getNPC(this.npcs.get(index));
    }

    public void getNPC(UUID id) {
        BlockPartyMessages.send(new CNPCRequest(id));
    }

    public void setNPC(M npc) {
        this.npc = npc;
        this.index = this.npcs.indexOf(this.npc.getID());
        this.setNPC();
    }

    public abstract void setNPC();
}
