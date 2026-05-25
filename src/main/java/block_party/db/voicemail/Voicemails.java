package block_party.db.voicemail;

import block_party.utils.NBT;
import block_party.scene.Speaker;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class Voicemails extends SavedData {
    public static final String KEY = "BlockParty_Voicemails";
    public static final Factory<Voicemails> FACTORY = new Factory<>(
            Voicemails::new,
            Voicemails::load);

    private final List<Entry> entries = new ArrayList<>();

    public static Voicemails get(Level level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(FACTORY, KEY);
    }

    public static Voicemails load(CompoundTag compound, HolderLookup.Provider provider) {
        Voicemails data = new Voicemails();
        compound.getList("Voicemails", NBT.COMPOUND).forEach(element -> data.entries.add(Entry.load((CompoundTag) element)));
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        this.entries.forEach(entry -> list.add(entry.save()));
        compound.put("Voicemails", list);
        return compound;
    }

    public void add(UUID owner, long npcId, String text, boolean tooltip, Speaker speaker, ResourceLocation sound, long delayMillis) {
        long createdAt = System.currentTimeMillis();
        this.entries.add(new Entry(owner, npcId, text, tooltip, speaker, sound, createdAt, createdAt + Math.max(0L, delayMillis), false, false));
        this.setDirty();
    }

    public int reveal(UUID owner, long now) {
        int revealed = 0;
        for (int index = 0; index < this.entries.size(); ++index) {
            Entry entry = this.entries.get(index);
            if (entry.deleted() || entry.revealed() || !owner.equals(entry.owner()) || now < entry.availableAt()) {
                continue;
            }
            this.entries.set(index, entry.reveal());
            ++revealed;
        }
        if (revealed > 0) {
            this.setDirty();
        }
        return revealed;
    }

    public List<Entry> revealed(UUID owner) {
        return this.entries.stream()
                .filter(entry -> !entry.deleted() && entry.revealed() && owner.equals(entry.owner()))
                .toList();
    }

    public void delete(Entry target) {
        for (int index = 0; index < this.entries.size(); ++index) {
            Entry entry = this.entries.get(index);
            if (entry.equals(target) && !entry.deleted()) {
                this.entries.set(index, entry.delete());
                this.setDirty();
                return;
            }
        }
    }

    public List<Entry> allForTests() {
        return List.copyOf(this.entries);
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level) {
            get(level).reveal(event.getEntity().getUUID(), System.currentTimeMillis());
        }
    }

    public record Entry(UUID owner, long npcId, String text, boolean tooltip, Speaker speaker, ResourceLocation sound, long createdAt, long availableAt, boolean revealed, boolean deleted) {
        private CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Owner", this.owner);
            tag.putLong("NpcId", this.npcId);
            tag.putString("Text", this.text);
            tag.putBoolean("Tooltip", this.tooltip);
            tag.put("Speaker", this.speaker.write());
            tag.putString("Sound", this.sound == null ? "" : this.sound.toString());
            tag.putLong("CreatedAt", this.createdAt);
            tag.putLong("AvailableAt", this.availableAt);
            tag.putBoolean("Revealed", this.revealed);
            tag.putBoolean("Deleted", this.deleted);
            return tag;
        }

        private Entry reveal() {
            return new Entry(this.owner, this.npcId, this.text, this.tooltip, this.speaker, this.sound, this.createdAt, this.availableAt, true, this.deleted);
        }

        private Entry delete() {
            return new Entry(this.owner, this.npcId, this.text, this.tooltip, this.speaker, this.sound, this.createdAt, this.availableAt, this.revealed, true);
        }

        private static Entry load(CompoundTag tag) {
            ResourceLocation sound = ResourceLocation.tryParse(tag.getString("Sound"));
            String legacyScene = tag.getString("Scene");
            return new Entry(
                    tag.getUUID("Owner"),
                    tag.getLong("NpcId"),
                    tag.contains("Text") ? tag.getString("Text") : legacyScene,
                    tag.getBoolean("Tooltip"),
                    Speaker.read(tag.getCompound("Speaker")),
                    sound,
                    tag.getLong("CreatedAt"),
                    tag.getLong("AvailableAt"),
                    tag.getBoolean("Revealed"),
                    tag.getBoolean("Deleted"));
        }
    }
}
