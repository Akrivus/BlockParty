package block_party.scene.dialogue;

import block_party.client.animation.Animation;
import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.trait.Emotion;
import block_party.registry.CustomSounds;
import block_party.utils.JsonUtils;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;

import java.util.Map;

public class ClientDialogue {
    private final String text;
    private final boolean tooltip;
    private final Animation animation;
    private final Emotion emotion;
    private final SoundEvent voice;
    private final boolean isVoiceLine;
    private Map<ResponseIcon, String> responses;

    public ClientDialogue(String text, boolean tooltip, Animation animation, Emotion emotion, SoundEvent voice, boolean isVoiceLine) {
        this.text = text;
        this.tooltip = tooltip;
        this.animation = animation;
        this.emotion = emotion;
        this.voice = voice;
        this.isVoiceLine = isVoiceLine;
        this.responses = Maps.newHashMap();
    }

    public ClientDialogue(CompoundTag compound) {
        this.text = compound.getString("Text");
        this.tooltip = compound.getBoolean("Tooltip");
        this.animation = Animation.DEFAULT.fromKey(compound.getString("Animation"));
        this.emotion = Emotion.NORMAL.fromValue(compound.getString("Emotion"));
        this.voice = JsonUtils.getAs(JsonUtils.SOUND_EVENT, compound.getString("Voice"));
        this.isVoiceLine = compound.getBoolean("IsVoiceLine");

        Map<ResponseIcon, String> responses = Maps.newHashMap();
        ListTag list = compound.getList("Responses", NBT.COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag response = list.getCompound(i);
            ResponseIcon icon = ResponseIcon.CLOSE_DIALOGUE.fromValue(response.getString("Icon"));
            String text = null;
            if (response.contains("Text"))
                text = response.getString("Text");
            responses.put(icon, text);
        }
        this.responses = responses;
    }

    public CompoundTag write(CompoundTag compound) {
        compound.putString("Text", this.text);
        compound.putBoolean("Tooltip", this.tooltip);
        compound.putString("Animation", this.animation.name());
        compound.putString("Emotion", this.emotion.name());
        compound.putString("Voice", this.voice.getLocation().toString());
        compound.putBoolean("IsVoiceLine", this.isVoiceLine);

        ListTag list = new ListTag();
        for (ResponseIcon icon : this.responses.keySet()) {
            CompoundTag response = new CompoundTag();
            response.putString("Icon", icon.name());
            response.putString("Text", this.responses.get(icon));
            list.add(response);
        }
        compound.put("Responses", list);
        return compound;
    }

    public CompoundTag write() {
        return this.write(new CompoundTag());
    }

    public void stage(BlockPartyNPC npc) {
        npc.setAnimationKey(this.animation);
        npc.setEmotion(this.emotion);
    }

    public String getText() {
        return this.text;
    }

    public boolean isTooltip()  {
        return this.tooltip;
    }

    public SoundEvent getVoice() {
        return this.voice;
    }

    public boolean isVoiceLine() {
        return this.isVoiceLine;
    }

    public Map<ResponseIcon, String> getResponses() {
        return this.responses;
    }

    public void add(ResponseIcon icon, String text) {
        this.responses.put(icon, text);
    }

}
