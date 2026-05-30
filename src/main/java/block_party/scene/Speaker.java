package block_party.scene;

import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record Speaker(
        Identity identity,
        Position position,
        String animation,
        String emotion,
        boolean speaks,
        ResourceLocation voice,
        float scale) {
    public static final Speaker DEFAULT = new Speaker(Identity.CHARACTER, Position.LEFT, "DEFAULT", "NORMAL", false, null, 1.0F);

    public static Speaker read(CompoundTag tag) {
        boolean speaks = tag.getBoolean("Speaks");
        return new Speaker(
                Identity.CHARACTER.fromValue(tag.getString("Identity")),
                Position.LEFT.fromValue(tag.getString("Position")),
                emptyDefault(tag.getString("Animation"), "DEFAULT"),
                emptyDefault(tag.getString("Emotion"), "NORMAL"),
                speaks,
                speaks ? parseId(tag.getString("Voice")) : null,
                tag.contains("Scale") ? tag.getFloat("Scale") : 1.0F);
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Identity", this.identity.name());
        tag.putString("Position", this.position.name());
        tag.putString("Animation", this.animation);
        tag.putString("Emotion", this.emotion);
        tag.putFloat("Scale", this.scale);
        tag.putBoolean("Speaks", this.speaks);
        if (this.speaks && this.voice != null) {
            tag.putString("Voice", this.voice.toString());
        }
        return tag;
    }

    private static String emptyDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static ResourceLocation parseId(String value) {
        return value == null || value.isBlank() ? null : ResourceLocation.tryParse(value);
    }

    public enum Identity {
        NARRATOR,
        CHARACTER;

        public Identity fromValue(String key) {
            try {
                return Identity.valueOf(key.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return this;
            }
        }
    }

    public enum Position {
        LEFT,
        CENTER,
        RIGHT;

        public Position fromValue(String key) {
            try {
                return Position.valueOf(key.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return this;
            }
        }
    }
}
