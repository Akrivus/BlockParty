package moeblocks.client.screen;

import moeblocks.datingsim.CacheNPC;
import moeblocks.init.MoeMessages;
import moeblocks.message.CNPCRequest;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.UUID;

public abstract class ControllerScreen extends AbstractScreen {
    protected List<UUID> npcs;
    protected CacheNPC npc;
    protected int index;
    
    protected ControllerScreen(List<UUID> npcs, UUID uuid, int x, int y) {
        super(x, y);
        this.npcs = npcs;
        this.index = this.npcs.indexOf(uuid);
        if (this.index < 0) {
            this.index = 0;
        }
    }
    
    public void getNPC(UUID uuid) {
        MoeMessages.send(new CNPCRequest(uuid));
    }
    
    public void setNPC(CacheNPC npc) {
        this.npc = npc;
        this.index = this.npcs.indexOf(npc.getUUID());
        this.setNPC();
    }
    
    public abstract void setNPC();
}
