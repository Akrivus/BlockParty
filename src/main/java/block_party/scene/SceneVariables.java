package block_party.scene;

import block_party.scene.data.Cookies;
import block_party.scene.data.Counters;
import block_party.scene.data.Locations;
import block_party.scene.data.Targets;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class SceneVariables extends SavedData {
    public static final String KEY = "BlockParty_SceneVariables";
    public static final Factory<SceneVariables> FACTORY = new Factory<>(
            SceneVariables::new,
            SceneVariables::load);

    private final Map<ScopedKey, Cookies> cookies = Maps.newHashMap();
    private final Map<ScopedKey, Counters> counters = Maps.newHashMap();
    private final Map<Long, Locations> locations = Maps.newHashMap();
    private final Map<Long, Targets> targets = Maps.newHashMap();

    public static SceneVariables get(Level level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(FACTORY, KEY);
    }

    public static SceneVariables load(CompoundTag compound, HolderLookup.Provider provider) {
        SceneVariables data = new SceneVariables();
        readLegacyMap(compound, "Cookies", data.cookies, Cookies::new);
        readLegacyMap(compound, "Counters", data.counters, Counters::new);
        readScopedMap(compound, "ScopedCookies", data.cookies, Cookies::new);
        readScopedMap(compound, "ScopedCounters", data.counters, Counters::new);
        readMap(compound, "Locations", data.locations, Locations::new);
        readMap(compound, "Targets", data.targets, Targets::new);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        compound.put("Cookies", writeLegacyMap(this.cookies));
        compound.put("Counters", writeLegacyMap(this.counters));
        compound.put("ScopedCookies", writeScopedMap(this.cookies));
        compound.put("ScopedCounters", writeScopedMap(this.counters));
        compound.put("Locations", writeMap(this.locations));
        compound.put("Targets", writeMap(this.targets));
        return compound;
    }

    public SceneVariableStore npc(long id) {
        return this.store(ScopedKey.npc(id));
    }

    public SceneVariableStore player(UUID player) {
        return this.store(ScopedKey.player(player));
    }

    public SceneVariableStore world() {
        return this.store(ScopedKey.world());
    }

    public Cookies cookies(long id) {
        return this.npc(id).cookies();
    }

    public Counters counters(long id) {
        return this.npc(id).counters();
    }

    public Cookies playerCookies(UUID player) {
        return this.player(player).cookies();
    }

    public Counters playerCounters(UUID player) {
        return this.player(player).counters();
    }

    public Cookies worldCookies() {
        return this.world().cookies();
    }

    public Counters worldCounters() {
        return this.world().counters();
    }

    private SceneVariableStore store(ScopedKey key) {
        this.setDirty();
        return new SceneVariableStore(
                this.cookies.computeIfAbsent(key, ignored -> new Cookies()),
                this.counters.computeIfAbsent(key, ignored -> new Counters()));
    }

    public Locations locations(long id) {
        this.setDirty();
        return this.locations.computeIfAbsent(id, ignored -> new Locations());
    }

    public Targets targets(long id) {
        this.setDirty();
        return this.targets.computeIfAbsent(id, ignored -> new Targets());
    }

    private static <T> void readMap(
            CompoundTag compound,
            String key,
            Map<Long, T> map,
            java.util.function.Function<CompoundTag, T> reader) {
        compound.getList(key, NBT.COMPOUND).forEach(element -> {
            CompoundTag member = (CompoundTag) element;
            map.put(member.getLong("UUID"), reader.apply(member));
        });
    }

    private static <T> void readLegacyMap(
            CompoundTag compound,
            String key,
            Map<ScopedKey, T> map,
            java.util.function.Function<CompoundTag, T> reader) {
        compound.getList(key, NBT.COMPOUND).forEach(element -> {
            CompoundTag member = (CompoundTag) element;
            map.put(ScopedKey.npc(member.getLong("UUID")), reader.apply(member));
        });
    }

    private static <T> void readScopedMap(
            CompoundTag compound,
            String key,
            Map<ScopedKey, T> map,
            java.util.function.Function<CompoundTag, T> reader) {
        compound.getList(key, NBT.COMPOUND).forEach(element -> {
            CompoundTag member = (CompoundTag) element;
            SceneVariableScope scope = SceneVariableScope.fromValue(member.getString("Scope"), SceneVariableScope.NPC);
            String id = member.getString("Id");
            if (!id.isBlank()) {
                map.put(new ScopedKey(scope, id), reader.apply(member));
            }
        });
    }

    private static <T extends block_party.scene.data.AbstractVariables<?>> ListTag writeLegacyMap(Map<ScopedKey, T> map) {
        ListTag list = new ListTag();
        map.forEach((key, variables) -> {
            if (key.scope() == SceneVariableScope.NPC) {
                CompoundTag member = variables.save();
                member.putLong("UUID", Long.parseLong(key.id()));
                list.add(member);
            }
        });
        return list;
    }

    private static <T extends block_party.scene.data.AbstractVariables<?>> ListTag writeScopedMap(Map<ScopedKey, T> map) {
        ListTag list = new ListTag();
        map.forEach((key, variables) -> {
            if (key.scope() != SceneVariableScope.NPC) {
                CompoundTag member = variables.save();
                member.putString("Scope", key.scope().serializedName());
                member.putString("Id", key.id());
                list.add(member);
            }
        });
        return list;
    }

    private static <T extends block_party.scene.data.AbstractVariables<?>> ListTag writeMap(Map<Long, T> map) {
        ListTag list = new ListTag();
        map.forEach((uuid, variables) -> {
            CompoundTag member = variables.save();
            member.putLong("UUID", uuid);
            list.add(member);
        });
        return list;
    }

    private record ScopedKey(SceneVariableScope scope, String id) {
        private ScopedKey {
            id = id.toLowerCase(Locale.ROOT);
        }

        static ScopedKey npc(long id) {
            return new ScopedKey(SceneVariableScope.NPC, Long.toString(id));
        }

        static ScopedKey player(UUID player) {
            return new ScopedKey(SceneVariableScope.PLAYER, player.toString());
        }

        static ScopedKey world() {
            return new ScopedKey(SceneVariableScope.WORLD, "world");
        }
    }
}
