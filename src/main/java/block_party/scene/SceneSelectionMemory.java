package block_party.scene;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class SceneSelectionMemory {
    private final Map<ResourceLocation, Long> cooldowns = new HashMap<>();
    private final Map<String, ResourceLocation> lastByGroup = new HashMap<>();

    public boolean eligible(Scene scene, long gameTime) {
        return gameTime >= this.cooldowns.getOrDefault(scene.id(), 0L);
    }

    public boolean repeatedInGroup(Scene scene) {
        String group = scene.selection().group();
        return !group.isBlank() && scene.id().equals(this.lastByGroup.get(group));
    }

    public void record(Scene scene, long gameTime) {
        if (scene.selection().cooldownTicks() > 0) {
            this.cooldowns.put(scene.id(), gameTime + scene.selection().cooldownTicks());
        }
        if (!scene.selection().group().isBlank()) {
            this.lastByGroup.put(scene.selection().group(), scene.id());
        }
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        CompoundTag cooldownTag = new CompoundTag();
        this.cooldowns.forEach((id, time) -> cooldownTag.putLong(id.toString(), time));
        tag.put("Cooldowns", cooldownTag);
        CompoundTag groups = new CompoundTag();
        this.lastByGroup.forEach((group, id) -> groups.putString(group, id.toString()));
        tag.put("Groups", groups);
        return tag;
    }

    public void read(CompoundTag tag) {
        this.cooldowns.clear();
        this.lastByGroup.clear();
        CompoundTag cooldownTag = tag.getCompound("Cooldowns");
        for (String key : cooldownTag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id != null) this.cooldowns.put(id, cooldownTag.getLong(key));
        }
        CompoundTag groups = tag.getCompound("Groups");
        for (String key : groups.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(groups.getString(key));
            if (id != null) this.lastByGroup.put(key, id);
        }
    }
}
