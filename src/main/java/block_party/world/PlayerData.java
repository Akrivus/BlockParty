package block_party.world;

import block_party.scene.dialogue.Cookies;
import block_party.scene.dialogue.Counters;
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

public class PlayerData extends SavedData {
    public static String KEY = "blockparty_playerdata";
    private final Map<UUID, Cookies> cookies = Maps.newHashMap();
    private final Map<UUID, Counters> counters = Maps.newHashMap();

    public PlayerData() { }

    public PlayerData(CompoundTag compound) {
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

    public static PlayerData load(CompoundTag compound) {
        return new PlayerData(compound);
    }

    public static PlayerData get(Level level) {
        try {
            ServerLevel server = level.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = server.getDataStorage();
            return storage.computeIfAbsent(PlayerData::load, PlayerData::new, KEY);
        } catch (NullPointerException e) {
            return new PlayerData();
        }
    }

    public static Cookies getCookiesFor(ServerPlayer player) {
        PlayerData data = get(player.getLevel());
        Cookies cookies = data.cookies.get(player.getUUID());
        if (cookies == null) {
            cookies = new Cookies();
            data.cookies.put(player.getUUID(), cookies);
        }
        return cookies;
    }

    public static Counters getCountersFor(ServerPlayer player) {
        PlayerData data = get(player.getLevel());
        Counters counters = data.counters.get(player.getUUID());
        if (counters == null) {
            counters = new Counters();
            data.counters.put(player.getUUID(), counters);
        }
        return counters;
    }

    public static void saveFor(ServerPlayer player) {
        get(player.getLevel()).setDirty();
    }
}
