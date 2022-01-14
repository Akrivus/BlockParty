package block_party.scene;

import block_party.client.animation.Animation;
import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.trait.Emotion;
import block_party.registry.SceneActions;
import block_party.utils.JsonUtils;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;

import java.util.Map;

public class Dialogue implements ISceneAction {
    public Map<Response.Icon, Response> responses;
    protected boolean tooltip;
    protected String text;
    protected Animation animation;
    protected Emotion emotion;
    protected SoundEvent voice;
    protected boolean voiceLine;
    private Response.Icon response;
    private BlockPartyNPC npc;

    @Override
    public void apply(BlockPartyNPC npc) {
        this.npc = npc;
        npc.setDialogue(this);
    }

    @Override
    public boolean isComplete() {
        return this.response != null;
    }

    @Override
    public void onComplete() {
        Response response = this.responses.get(this.response);
        this.npc.automaton.putAction(response == null ? SceneActions.get(SceneActions.RESET) : response);
        this.npc.setDialogue(null);
    }

    @Override
    public void parse(JsonObject json) {
        this.tooltip = GsonHelper.getAsBoolean(json, "tooltip", false);
        this.text = GsonHelper.getAsString(json, "text");
        this.animation = Animation.DEFAULT.fromKey(JsonUtils.getAsResourceLocation(json, "animation", "default"));
        this.emotion = Emotion.NORMAL.fromValue(JsonUtils.getAsResourceLocation(json, "emotion", "normal"));
        this.voiceLine = json.has("voice");
        if (this.voiceLine) { this.voice = JsonUtils.getAs(JsonUtils.SOUND_EVENT, json, "voice"); }

        this.responses = Maps.newHashMap();

        JsonArray responses = json.getAsJsonArray("responses");
        for (int i = 0; i < responses.size(); ++i) {
            Response response = new Response();
            JsonElement element = responses.get(i);
            response.parse(element.getAsJsonObject());
            this.responses.put(response.getIcon(), response);
        }
    }

    public String getText() {
        return this.text;
    }

    public boolean isTooltip() {
        return this.tooltip;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public Emotion getEmotion() {
        return this.emotion;
    }

    public SoundEvent getVoiceLine() {
        return this.voice;
    }

    public boolean hasVoiceLine() {
        return this.voiceLine;
    }

    public void setResponse(Response.Icon icon) {
        this.response = icon;
    }

    public static class Model {
        private final String text;
        private final boolean tooltip;
        private final Animation animation;
        private final Emotion emotion;
        private final SoundEvent voice;
        private final boolean isVoiceLine;
        private Map<Response.Icon, String> responses;
    
        public Model(String text, boolean tooltip, Animation animation, Emotion emotion, SoundEvent voice, boolean isVoiceLine) {
            this.text = text;
            this.tooltip = tooltip;
            this.animation = animation;
            this.emotion = emotion;
            this.voice = voice;
            this.isVoiceLine = isVoiceLine;
            this.responses = Maps.newHashMap();
        }
    
        public Model(CompoundTag compound) {
            this.text = compound.getString("Text");
            this.tooltip = compound.getBoolean("Tooltip");
            this.animation = Animation.DEFAULT.fromKey(compound.getString("Animation"));
            this.emotion = Emotion.NORMAL.fromValue(compound.getString("Emotion"));
            this.voice = JsonUtils.getAs(JsonUtils.SOUND_EVENT, compound.getString("Voice"));
            this.isVoiceLine = compound.getBoolean("IsVoiceLine");
    
            Map<Response.Icon, String> responses = Maps.newHashMap();
            ListTag list = compound.getList("Responses", NBT.COMPOUND);
            for (int i = 0; i < list.size(); ++i) {
                CompoundTag response = list.getCompound(i);
                Response.Icon icon = Response.Icon.CLOSE_DIALOGUE.fromValue(response.getString("Icon"));
                String text = null;
                if (response.contains("Text")) { text = response.getString("Text"); }
                responses.put(icon, text);
            }
            this.responses = responses;
        }
    
        public CompoundTag write() {
            return this.write(new CompoundTag());
        }
    
        public CompoundTag write(CompoundTag compound) {
            compound.putString("Text", this.text);
            compound.putBoolean("Tooltip", this.tooltip);
            compound.putString("Animation", this.animation.name());
            compound.putString("Emotion", this.emotion.name());
            compound.putString("Voice", this.voice.getLocation().toString());
            compound.putBoolean("IsVoiceLine", this.isVoiceLine);
    
            ListTag list = new ListTag();
            for (Response.Icon icon : this.responses.keySet()) {
                CompoundTag response = new CompoundTag();
                response.putString("Icon", icon.name());
                response.putString("Text", this.responses.get(icon));
                list.add(response);
            }
            compound.put("Responses", list);
            return compound;
        }
    
        public void stage(BlockPartyNPC npc) {
            npc.setAnimationKey(this.animation);
            npc.setEmotion(this.emotion);
        }
    
        public String getText() {
            return this.text;
        }
    
        public boolean isTooltip() {
            return this.tooltip;
        }
    
        public SoundEvent getVoice() {
            return this.voice;
        }
    
        public boolean isVoiceLine() {
            return this.isVoiceLine;
        }
    
        public Map<Response.Icon, String> getResponses() {
            return this.responses;
        }
    
        public void add(Response.Icon icon, String text) {
            this.responses.put(icon, text);
        }
    
    }
}
