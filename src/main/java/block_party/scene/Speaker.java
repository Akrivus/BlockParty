package block_party.scene;

import block_party.client.animation.Animation;
import block_party.entities.BlockPartyNPC;
import block_party.scene.filters.Emotion;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;

public class Speaker {
    public Identity identity;
    public Position position;
    public Animation animation;
    public Emotion emotion;
    public SoundEvent voice;
    public boolean speaks;
    public float scale;

    public Speaker(JsonObject json) {
        this.identity = Identity.CHARACTER.fromValue(GsonHelper.getAsString(json, "identity", "character"));
        this.position = Position.LEFT.fromValue(GsonHelper.getAsString(json, "position", "left"));
        this.animation = Animation.DEFAULT.fromKey(GsonHelper.getAsString(json, "animation", "default"));
        this.emotion = Emotion.NORMAL.fromValue(GsonHelper.getAsString(json, "emotion", "normal"));
        this.scale = GsonHelper.getAsFloat(json, "scale", 1.0F);
        this.speaks = json.has("voice");
        if (this.speaks) {
            this.voice = JsonUtils.getAs(JsonUtils.SOUND_EVENT, GsonHelper.getAsString(json, "voice"));
        }
    }

    public Speaker(CompoundTag tag) {
        this.identity = Identity.CHARACTER.fromValue(tag.getString("Identity"));
        this.position = Position.LEFT.fromValue(tag.getString("Position"));
        this.animation = Animation.DEFAULT.fromKey(tag.getString("Animation"));
        this.emotion = Emotion.NORMAL.fromValue(tag.getString("Emotion"));
        this.scale = tag.getFloat("Scale");
        this.speaks = tag.getBoolean("Speaks");
        if (this.speaks) {
            this.voice = JsonUtils.getAs(JsonUtils.SOUND_EVENT, tag.getString("Voice"));
        }
    }

    public CompoundTag write(CompoundTag tag) {
        tag.putString("Identity", this.identity.name());
        tag.putString("Position", this.position.name());
        tag.putString("Animation", this.animation.name());
        tag.putString("Emotion", this.emotion.name());
        tag.putFloat("Scale", this.scale);
        tag.putBoolean("Speaks", this.speaks);
        if (this.speaks) {
            tag.putString("Voice", this.voice.getLocation().toString());
        }
        return tag;
    }

    public void stage(BlockPartyNPC npc) {
        npc.setAnimationKey(this.animation);
        npc.setEmotion(this.emotion);
    }

    public enum Identity {
        NARRATOR, CHARACTER;

        public Identity fromValue(String key) {
            try {
                return Identity.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                return this;
            }
        }
    }

    public enum Position {
        LEFT, CENTER, RIGHT;

        public Position fromValue(String key) {
            try {
                return Position.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                return this;
            }
        }
    }
}
