package moeblocks.convo;

import moeblocks.convo.enums.Response;
import moeblocks.data.AbstractNPC;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;

public class Dialogue {
    private final AbstractNPC speaker;
    private final String line;
    private final Response[] responses;

    public Dialogue(AbstractNPC speaker, String line, Response... responses) {
        this.speaker = speaker;
        this.line = line;
        this.responses = responses;
    }

    public Dialogue(CompoundNBT compound) {
        this.speaker = AbstractNPC.create(compound);
        this.line = compound.getString("Line");
        ListNBT list = compound.getList("Responses", Constants.NBT.TAG_STRING);
        this.responses = new Response[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            this.responses[i] = Response.valueOf(list.getString(i));
        }
    }

    public AbstractNPC getSpeaker() {
        return this.speaker;
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
        this.speaker.write(compound);
        compound.putString("Line", this.line);
        ListNBT list = new ListNBT();
        for (Response response : this.responses) {
            list.add(StringNBT.valueOf(response.toString()));
        }
        compound.put("Responses", list);
        return compound;
    }
}
