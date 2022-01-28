package block_party.scene;

import block_party.entities.BlockPartyNPC;
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

public class CookieJar extends SavedData {
    public static String KEY = "blockparty_cookiejar";
    private final Map<Long, Cookies> cookies = Maps.newHashMap();
    private final Map<Long, Counters> counters = Maps.newHashMap();

    public CookieJar() { }

    public CookieJar(CompoundTag compound) {
        ListTag cookies = compound.getList("Cookies", NBT.COMPOUND);
        cookies.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            long uuid = member.getLong("UUID");
            this.cookies.put(uuid, new Cookies(member));
        });
        ListTag counters = compound.getList("Counters", NBT.COMPOUND);
        counters.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            long uuid = member.getLong("UUID");
            this.counters.put(uuid, new Counters(member));
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
        return compound;
    }

    public static CookieJar get(Level level) {
        try {
            ServerLevel server = level.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = server.getDataStorage();
            return storage.computeIfAbsent(CookieJar::new, CookieJar::new, KEY);
        } catch (NullPointerException e) {
            return new CookieJar();
        }
    }

    public static Cookies cookies(Level level, Long id) {
        CookieJar data = get(level);
        Cookies cookies = data.cookies.get(id);
        if (cookies == null) { cookies = new Cookies(); }
        data.cookies.put(id, cookies);
        return cookies;
    }

    public static Cookies getCookies(ServerPlayer player) {
        Cookies cookies = cookies(player.getLevel(), player.getUUID().getMostSignificantBits() & Long.MAX_VALUE);
        cookies.add("name",        player.getGameProfile().getName());
        cookies.add("server",      player.getServer().name());
        return cookies;
    }

    public static Cookies getCookies(BlockPartyNPC npc) {
        Cookies cookies = cookies(npc.getLevel(), npc.getDatabaseID());
        cookies.add("name",        npc.getGivenName());
        cookies.add("family_name", npc.getFamilyName());
        cookies.add("blood_type",  Trans.late(npc.getBloodType().getTranslationKey()));
        cookies.add("dere",        Trans.late(npc.getDere().getTranslationKey()));
        cookies.add("emotion",     Trans.late(npc.getEmotion().getTranslationKey()));
        cookies.add("gender",      Trans.late(npc.getGender().getTranslationKey()));
        return cookies;
    }

    public static Counters counters(Level level, Long id) {
        CookieJar data = get(level);
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

    public static void save(Level level) {
        get(level).setDirty();
    }
}
