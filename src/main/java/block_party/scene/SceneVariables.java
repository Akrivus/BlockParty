package block_party.scene;

import block_party.entities.BlockPartyNPC;
import block_party.scene.data.Cookies;
import block_party.scene.data.Counters;
import block_party.scene.data.Locations;
import block_party.scene.data.Targets;
import block_party.utils.NBT;
import block_party.utils.Trans;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Map;

public class SceneVariables extends SavedData {
    public static String KEY = "BlockParty_SceneVariables";
    private final Map<Long, Cookies>   cookies   = Maps.newHashMap();
    private final Map<Long, Counters>  counters  = Maps.newHashMap();
    private final Map<Long, Locations> locations = Maps.newHashMap();
    private final Map<Long, Targets>   targets   = Maps.newHashMap();

    public SceneVariables() { }

    public SceneVariables(CompoundTag compound) {
        ListTag list;
        list = compound.getList("Cookies", NBT.COMPOUND);
        list.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            long uuid = member.getLong("UUID");
            this.cookies.put(uuid, new Cookies(member));
        });
        list = compound.getList("Counters", NBT.COMPOUND);
        list.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            long uuid = member.getLong("UUID");
            this.counters.put(uuid, new Counters(member));
        });
        list = compound.getList("Locations", NBT.COMPOUND);
        list.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            long uuid = member.getLong("UUID");
            this.locations.put(uuid, new Locations(member));
        });
        list = compound.getList("Targets", NBT.COMPOUND);
        list.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            long uuid = member.getLong("UUID");
            this.targets.put(uuid, new Targets(member));
        });
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag cookies = new ListTag();
        this.cookies.forEach((uuid, list) -> {
            CompoundTag member = list.save();
            member.putLong("UUID", uuid);
            cookies.add(member);
        });
        compound.put("Cookies", cookies);
        ListTag counters = new ListTag();
        this.counters.forEach((uuid, list) -> {
            CompoundTag member = list.save();
            member.putLong("UUID", uuid);
            counters.add(member);
        });
        compound.put("Counters", counters);
        ListTag locations = new ListTag();
        this.locations.forEach((uuid, list) -> {
            CompoundTag member = list.save();
            member.putLong("UUID", uuid);
            locations.add(member);
        });
        compound.put("Locations", locations);
        ListTag targets = new ListTag();
        this.targets.forEach((uuid, list) -> {
            CompoundTag member = list.save();
            member.putLong("UUID", uuid);
            targets.add(member);
        });
        compound.put("Targets", targets);
        return compound;
    }

    public static SceneVariables get(Level level) {
        try {
            ServerLevel server = level.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = server.getDataStorage();
            return storage.computeIfAbsent(SceneVariables::new, SceneVariables::new, KEY);
        } catch (NullPointerException e) {
            return new SceneVariables();
        }
    }

    public static Cookies cookies(Level level, Long id) {
        SceneVariables data = get(level);
        Cookies cookies = data.cookies.get(id);
        if (cookies == null) { cookies = new Cookies(); }
        data.cookies.put(id, cookies);
        return cookies;
    }

    public static Cookies getCookies(ServerPlayer player) {
        Cookies cookies = cookies(player.getLevel(), player.getUUID().getMostSignificantBits() & Long.MAX_VALUE);
        cookies.set("name",        player.getGameProfile().getName());
        cookies.set("server",      player.getServer().name());
        return cookies;
    }

    public static Cookies getCookies(BlockPartyNPC npc) {
        Cookies cookies = cookies(npc.getLevel(), npc.getDatabaseID());
        cookies.set("name",        npc.getGivenName());
        cookies.set("family_name", npc.getFamilyName());
        cookies.set("blood_type",  Trans.late(npc.getBloodType().getTranslationKey()));
        cookies.set("dere",        Trans.late(npc.getDere().getTranslationKey()));
        cookies.set("emotion",     Trans.late(npc.getEmotion().getTranslationKey()));
        cookies.set("gender",      Trans.late(npc.getGender().getTranslationKey()));
        return cookies;
    }

    public static Counters counters(Level level, Long id) {
        SceneVariables data = get(level);
        Counters counters = data.counters.get(id);
        if (counters == null) { counters = new Counters(); }
        data.counters.put(id, counters);
        return counters;
    }

    public static Counters getCounters(ServerPlayer player) {
        Counters counters = counters(player.getLevel(), player.getUUID().getMostSignificantBits() & Long.MAX_VALUE);
        counters.set("health",     (int) player.getHealth());
        counters.set("exhaustion", (int) player.getFoodData().getExhaustionLevel());
        counters.set("saturation", (int) player.getFoodData().getSaturationLevel());
        counters.set("food_level", player.getFoodData().getFoodLevel());
        counters.set("air_supply", player.getAirSupply());
        return counters;
    }

    public static Counters getCounters(BlockPartyNPC npc) {
        Counters counters = counters(npc.getLevel(), npc.getDatabaseID());
        counters.set("health",     (int) npc.getHealth());
        counters.set("food_level", (int) npc.getFoodLevel());
        counters.set("exhaustion", (int) npc.getExhaustion());
        counters.set("saturation", (int) npc.getSaturation());
        counters.set("stress",     (int) npc.getStress());
        counters.set("relaxation", (int) npc.getRelaxation());
        counters.set("loyalty",    (int) npc.getLoyalty());
        counters.set("affection",  (int) npc.getAffection());
        counters.set("slouch",     (int) npc.getSlouch());
        counters.set("scale",      (int) npc.getScale());
        counters.set("age",        (int) npc.getAgeInYears());
        return counters;
    }

    public static Locations locations(Level level, Long id) {
        SceneVariables data = get(level);
        Locations locations = data.locations.get(id);
        if (locations == null) { locations = new Locations(); }
        data.locations.put(id, locations);
        return locations;
    }

    public static Locations getLocations(ServerPlayer player) {
        return locations(player.getLevel(), player.getUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }

    public static Locations getLocations(BlockPartyNPC npc) {
        return locations(npc.getLevel(), npc.getDatabaseID());
    }

    public static Targets targets(Level level, Long id) {
        SceneVariables data = get(level);
        Targets targets = data.targets.get(id);
        if (targets == null) { targets = new Targets(); }
        data.targets.put(id, targets);
        return targets;
    }

    public static Targets getTargets(ServerPlayer player) {
        return targets(player.getLevel(), player.getUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }

    public static Targets getTargets(BlockPartyNPC npc) {
        return targets(npc.getLevel(), npc.getDatabaseID());
    }

    public static void save(Level level) {
        get(level).setDirty();
    }
}
