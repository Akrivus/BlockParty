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

import java.util.Map;

public final class SceneVariables extends SavedData {
    public static final String KEY = "BlockParty_SceneVariables";
    public static final Factory<SceneVariables> FACTORY = new Factory<>(
            SceneVariables::new,
            SceneVariables::load);

    private final Map<Long, Cookies> cookies = Maps.newHashMap();
    private final Map<Long, Counters> counters = Maps.newHashMap();
    private final Map<Long, Locations> locations = Maps.newHashMap();
    private final Map<Long, Targets> targets = Maps.newHashMap();

    public static SceneVariables get(Level level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(FACTORY, KEY);
    }

    public static SceneVariables load(CompoundTag compound, HolderLookup.Provider provider) {
        SceneVariables data = new SceneVariables();
        readMap(compound, "Cookies", data.cookies, Cookies::new);
        readMap(compound, "Counters", data.counters, Counters::new);
        readMap(compound, "Locations", data.locations, Locations::new);
        readMap(compound, "Targets", data.targets, Targets::new);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        compound.put("Cookies", writeMap(this.cookies));
        compound.put("Counters", writeMap(this.counters));
        compound.put("Locations", writeMap(this.locations));
        compound.put("Targets", writeMap(this.targets));
        return compound;
    }

    public Cookies cookies(long id) {
        this.setDirty();
        return this.cookies.computeIfAbsent(id, ignored -> new Cookies());
    }

    public Counters counters(long id) {
        this.setDirty();
        return this.counters.computeIfAbsent(id, ignored -> new Counters());
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

    private static <T extends block_party.scene.data.AbstractVariables<?>> ListTag writeMap(Map<Long, T> map) {
        ListTag list = new ListTag();
        map.forEach((uuid, variables) -> {
            CompoundTag member = variables.save();
            member.putLong("UUID", uuid);
            list.add(member);
        });
        return list;
    }
}
