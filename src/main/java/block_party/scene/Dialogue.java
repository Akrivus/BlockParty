package block_party.scene;

import block_party.utils.JsonUtils;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;

import java.util.Map;

public class Dialogue {
    private final Map<Response, String> responses;
    private final String text;
    private final boolean tooltip;
    private final Speaker speaker;
    private final SoundEvent sound;

    public Dialogue(String text, boolean tooltip, Speaker speaker, SoundEvent sound) {
        this.responses = Maps.newHashMap();
        this.text = text;
        this.tooltip = tooltip;
        this.speaker = speaker;
        this.sound = sound;
    }

    public Dialogue(CompoundTag compound) {
        Map<Response, String> responses = Maps.newHashMap();
        ListTag list = compound.getList("Responses", NBT.COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag response = list.getCompound(i);
            Response icon = Response.CLOSE_DIALOGUE.fromValue(response.getString("Icon"));
            String text = null;
            if (response.contains("Text")) { text = response.getString("Text"); }
            responses.put(icon, text);
        }
        this.responses = responses;
        this.text = compound.getString("Text");
        this.tooltip = compound.getBoolean("Tooltip");
        this.speaker = new Speaker(compound.getCompound("Speaker"));
        this.sound = JsonUtils.getAs(JsonUtils.SOUND_EVENT, compound.getString("Sound"));
    }

    public CompoundTag write() {
        return this.write(new CompoundTag());
    }

    public CompoundTag write(CompoundTag compound) {
        ListTag list = new ListTag();
        for (Response icon : this.responses.keySet()) {
            CompoundTag response = new CompoundTag();
            response.putString("Icon", icon.name());
            response.putString("Text", this.responses.get(icon));
            list.add(response);
        }
        compound.put("Responses", list);
        compound.putString("Text", this.text);
        compound.putBoolean("Tooltip", this.tooltip);
        compound.put("Speaker", this.speaker.write(new CompoundTag()));
        compound.putString("Sound", this.sound.getLocation().toString());
        return compound;
    }

    public String getText() {
        return this.text;
    }

    public boolean isTooltip() {
        return this.tooltip;
    }

    public Speaker getSpeaker() {
        return this.speaker;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public Map<Response, String> getResponses() {
        return this.responses;
    }

    public void add(Response icon, String text) {
        this.responses.put(icon, text);
    }
}
