package moeblocks.datingsim.convo;

import moeblocks.datingsim.CacheNPC;
import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;

public class Dialogue {
    private final CacheNPC speaker;
    private final String line;
    private final Response[] responses;
    
    public Dialogue(CacheNPC speaker, String line, Response... responses) {
        this.speaker = speaker;
        this.line = line;
        this.responses = responses;
    }
    
    public Dialogue(CompoundNBT compound) {
        ListNBT list = compound.getList("Responses", Constants.NBT.TAG_STRING);
        this.speaker = new CacheNPC(compound.getCompound("NPC"));
        this.line = compound.getString("Line");
        this.responses = new Response[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            this.responses[i] = Response.valueOf(list.getString(i));
        }
    }
    
    public CacheNPC getSpeaker() {
        return this.speaker;
    }
    
    public AbstractNPCEntity getEntity(EntityType<? extends AbstractNPCEntity> type) {
        return this.speaker.clone(Minecraft.getInstance(), type);
    }
    
    public String getLine() {
        return this.line;
    }
    
    public boolean has(Response control) {
        for (Response response : this.responses) {
            if (response.equals(control)) { return true; }
        }
        return false;
    }
    
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        for (Response response : this.responses) { list.add(StringNBT.valueOf(response.toString())); }
        compound.put("NPC", this.speaker.write(new CompoundNBT()));
        compound.putString("Line", this.line);
        compound.put("Responses", list);
        return compound;
    }
}
