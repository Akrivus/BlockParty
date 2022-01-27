package block_party.scene;

import block_party.entities.BlockPartyNPC;
import block_party.registry.SceneActions;
import block_party.utils.JsonUtils;
import block_party.utils.Markdown;
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
import java.util.regex.Pattern;

public class Dialogue implements ISceneAction {
    private static final Pattern COUNTER_PATTERN = Pattern.compile("#(\\w+)");
    private static final Pattern PLAYER_COUNTER_PATTERN = Pattern.compile("#player\\.(\\w+)");
    private static final Pattern COOKIE_PATTERN = Pattern.compile("@(\\w+)");
    private static final Pattern PLAYER_COOKIE_PATTERN = Pattern.compile("@player\\.(\\w+)");
    public Map<Response.Icon, Response> responses;
    protected boolean tooltip;
    protected String text;
    protected Speaker speaker;
    private Response.Icon response;
    private BlockPartyNPC npc;

    @Override
    public void apply(BlockPartyNPC npc) {
        this.npc = npc;
        this.npc.setDialogue(this);
    }

    @Override
    public boolean isComplete() {
        return this.response != null;
    }

    @Override
    public void onComplete() {
        Response response = this.responses.get(this.response);
        this.npc.sceneManager.putAction(response == null ? SceneActions.build(SceneActions.END) : response);
        this.npc.setDialogue(null);
    }

    @Override
    public void parse(JsonObject json) {
        this.tooltip = GsonHelper.getAsBoolean(json, "tooltip", false);
        this.text = GsonHelper.getAsString(json, "text");
        this.speaker = new Speaker(json.getAsJsonObject("speaker"));
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
        if (this.npc == null) { return this.text; }
        String text = this.text;
        text = Markdown.highlight(text, COUNTER_PATTERN, "yellow", (match) -> String.valueOf(this.npc.sceneManager.counters.get(match)));
        text = Markdown.highlight(text, PLAYER_COUNTER_PATTERN, "yellow", (match) -> String.valueOf(PlayerSceneManager.getCountersFor(this.npc.getServerPlayer()).get(match)));
        text = Markdown.highlight(text, COOKIE_PATTERN, "magenta", (match) -> this.npc.sceneManager.cookies.get(match));
        text = Markdown.highlight(text, PLAYER_COOKIE_PATTERN, "magenta", (match) -> PlayerSceneManager.getCookiesFor(this.npc.getServerPlayer()).get(match));
        return Markdown.parse(text);
    }

    public boolean isTooltip() {
        return this.tooltip;
    }

    public Speaker getSpeaker() {
        return this.speaker;
    }

    public void setResponse(Response.Icon icon) {
        this.response = icon;
    }

    public static class Model {
        private final Map<Response.Icon, String> responses;
        private final String text;
        private final boolean tooltip;
        private final Speaker speaker;
        private final SoundEvent sound;
    
        public Model(String text, boolean tooltip, Speaker speaker, SoundEvent sound) {
            this.responses = Maps.newHashMap();
            this.text = text;
            this.tooltip = tooltip;
            this.speaker = speaker;
            this.sound = sound;
        }
    
        public Model(CompoundTag compound) {
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
            for (Response.Icon icon : this.responses.keySet()) {
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
    
        public Map<Response.Icon, String> getResponses() {
            return this.responses;
        }
    
        public void add(Response.Icon icon, String text) {
            this.responses.put(icon, text);
        }
    }
}
