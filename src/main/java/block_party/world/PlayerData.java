package block_party.world;

import block_party.scene.dialogue.CookieJar;
import block_party.scene.dialogue.Counter;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData extends SavedData {
    public static String KEY = "blockparty_playerdata";
    private final Map<UUID, List<CookieJar>> cookies = Maps.newHashMap();
    private final Map<UUID, List<Counter>> counters = Maps.newHashMap();

    @Override
    public CompoundTag save(CompoundTag compound) {
        return compound;
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

    public static PlayerData load(CompoundTag compound) {
        return new PlayerData();
    }
}
