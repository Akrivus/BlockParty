package block_party.scene;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public record Dialogue(String text, boolean tooltip, Speaker speaker, ResourceLocation sound, Map<Response, String> responses) {
    public Dialogue {
        speaker = speaker == null ? Speaker.DEFAULT : speaker;
        responses = Map.copyOf(new LinkedHashMap<>(responses));
    }

    public static Dialogue read(CompoundTag tag) {
        Map<Response, String> responses = new LinkedHashMap<>();
        ListTag list = tag.getList("Responses", 10);
        for (int index = 0; index < list.size(); ++index) {
            CompoundTag response = list.getCompound(index);
            Response icon = Response.CLOSE_DIALOGUE.fromValue(response.getString("Icon"));
            responses.put(icon, response.contains("Text") ? response.getString("Text") : null);
        }
        return new Dialogue(
                tag.getString("Text"),
                tag.getBoolean("Tooltip"),
                Speaker.read(tag.getCompound("Speaker")),
                parseId(tag.getString("Sound")),
                responses);
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (Map.Entry<Response, String> entry : this.responses.entrySet()) {
            CompoundTag response = new CompoundTag();
            response.putString("Icon", entry.getKey().name());
            if (entry.getValue() != null) {
                response.putString("Text", entry.getValue());
            }
            list.add(response);
        }
        tag.put("Responses", list);
        tag.putString("Text", this.text);
        tag.putBoolean("Tooltip", this.tooltip);
        tag.put("Speaker", this.speaker.write());
        tag.putString("Sound", this.sound == null ? "" : this.sound.toString());
        return tag;
    }

    private static ResourceLocation parseId(String value) {
        return value == null || value.isBlank() ? null : ResourceLocation.tryParse(value);
    }
}
