package block_party.scene;

import block_party.utils.JsonUtils;
import block_party.utils.NBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Map;

public class Dialogue {
    private final Map<Response, String> responses;
    private final String text;
    private final boolean tooltip;
    private final Speaker speaker;
    private SoundEvent sound;
    private ResourceLocation soundID;

    public Dialogue(String text, boolean tooltip, Speaker speaker, SoundEvent sound) {
        this.responses = new java.util.LinkedHashMap<>();
        this.text = text;
        this.tooltip = tooltip;
        this.speaker = speaker;
        this.sound = sound;
        this.soundID = sound == null ? null : sound.getLocation();
    }

    public Dialogue(CompoundTag compound) {
        Map<Response, String> responses = new java.util.LinkedHashMap<>();
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
        this.soundID = parseID(compound.getString("Sound"));
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
        ResourceLocation soundID = this.getSoundID();
        compound.putString("Sound", soundID == null ? "" : soundID.toString());
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
        if (this.sound == null && this.soundID != null) {
            this.sound = JsonUtils.getAs(JsonUtils.SOUND_EVENT, this.soundID);
        }
        return this.sound;
    }

    public ResourceLocation getSoundID() {
        if (this.soundID == null && this.sound != null) {
            this.soundID = this.sound.getLocation();
        }
        return this.soundID;
    }

    private static ResourceLocation parseID(String value) {
        return value == null || value.isEmpty() ? null : ResourceLocation.tryParse(value);
    }

    public Map<Response, String> getResponses() {
        return this.responses;
    }

    public void add(Response icon, String text) {
        this.responses.put(icon, text);
    }
}
