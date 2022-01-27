package block_party.scene;

import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Map;
import java.util.UUID;

public class PlayerSceneManager extends SavedData {
    public static String KEY = "blockparty_playerdata";
    private final Map<UUID, Cookies> cookies = Maps.newHashMap();
    private final Map<UUID, Counters> counters = Maps.newHashMap();

    public PlayerSceneManager() { }

    public PlayerSceneManager(CompoundTag compound) {
        ListTag cookies = compound.getList("PlayerCookies", NBT.COMPOUND);
        cookies.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            UUID uuid = member.getUUID("UUID");
            this.cookies.put(uuid, new Cookies(member));
        });
        ListTag counters = compound.getList("Counters", NBT.COMPOUND);
        counters.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            UUID uuid = member.getUUID("UUID");
            this.counters.put(uuid, new Counters(member));
        });
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag cookies = new ListTag();
        this.cookies.forEach((uuid, list) -> {
            CompoundTag member = list.save();
            member.putUUID("UUID", uuid);
            cookies.add(member);
        });
        compound.put("PlayerCookies", cookies);
        ListTag counters = new ListTag();
        this.counters.forEach((uuid, list) -> {
            CompoundTag member = list.save();
            member.putUUID("UUID", uuid);
            counters.add(member);
        });
        compound.put("Counters", counters);
        return compound;
    }

    public static PlayerSceneManager load(CompoundTag compound) {
        return new PlayerSceneManager(compound);
    }

    public static PlayerSceneManager get(Level level) {
        try {
            ServerLevel server = level.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = server.getDataStorage();
            return storage.computeIfAbsent(PlayerSceneManager::load, PlayerSceneManager::new, KEY);
        } catch (NullPointerException e) {
            return new PlayerSceneManager();
        }
    }

    public static Cookies getCookiesFor(ServerPlayer player) {
        PlayerSceneManager data = get(player.getLevel());
        Cookies cookies = data.cookies.get(player.getUUID());
        if (cookies == null) { cookies = new Cookies(); }
        data.cookies.put(player.getUUID(), cookies);
        cookies.add("name",   player.getGameProfile().getName());
        cookies.add("server", player.getServer().name());
        return cookies;
    }

    public static Counters getCountersFor(ServerPlayer player) {
        PlayerSceneManager data = get(player.getLevel());
        Counters counters = data.counters.get(player.getUUID());
        if (counters == null) { counters = new Counters(); }
        counters.set("health",     (int) player.getHealth());
        counters.set("exhaustion", (int) player.getFoodData().getExhaustionLevel());
        counters.set("saturation", (int) player.getFoodData().getSaturationLevel());
        counters.set("food_level", player.getFoodData().getFoodLevel());
        counters.set("air_supply", player.getAirSupply());
        data.counters.put(player.getUUID(), counters);
        return counters;
    }

    public static void saveFor(ServerPlayer player) {
        get(player.getLevel()).setDirty();
    }
}
