package block_party.scene.actions;

import block_party.client.animation.Animation;
import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.trait.Emotion;
import block_party.registry.SceneActions;
import block_party.scene.ISceneAction;
import block_party.scene.dialogue.ResponseIcon;
import block_party.utils.JsonUtils;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;

import java.util.Map;

public class DialogueAction implements ISceneAction {
    public Map<ResponseIcon, ResponseAction> responses;
    protected boolean tooltip;
    protected String text;
    protected Animation animation;
    protected Emotion emotion;
    protected SoundEvent voice;
    protected boolean voiceLine;
    private ResponseIcon response;
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
        ResponseAction response = this.responses.get(this.response);
        this.npc.automaton.putAction(response == null ? SceneActions.get(SceneActions.TRANSFER) : response);
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
            ResponseAction response = new ResponseAction();
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

    public void setResponse(ResponseIcon icon) {
        this.response = icon;
    }
}
