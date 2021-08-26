package block_party.convo;

import block_party.convo.enums.Response;
import block_party.db.records.NPC;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.common.util.Constants;

public class Dialogue {
    private final NPC speaker;
    private final String line;
    private final Response[] responses;

    public Dialogue(NPC speaker, String line, Response... responses) {
        this.speaker = speaker;
        this.line = line;
        this.responses = responses;
    }

    public Dialogue(CompoundTag compound) {
        this.speaker = NPC.create(compound);
        this.line = compound.getString("Line");
        ListTag list = compound.getList("Responses", Constants.NBT.TAG_STRING);
        this.responses = new Response[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            this.responses[i] = Response.valueOf(list.getString(i));
        }
    }

    public NPC getSpeaker() {
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

    public CompoundTag write(CompoundTag compound) {
        this.speaker.write(compound);
        compound.putString("Line", this.line);
        ListTag list = new ListTag();
        for (Response response : this.responses) {
            list.add(StringTag.valueOf(response.toString()));
        }
        compound.put("Responses", list);
        return compound;
    }
}
