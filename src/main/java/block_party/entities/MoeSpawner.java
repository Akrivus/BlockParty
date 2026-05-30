package block_party.entities;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.profile.MoeFamilyNames;
import block_party.db.DimBlockPos;
import block_party.registry.CustomEntities;
import block_party.registry.CustomTags;
import block_party.world.CellPhone;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class MoeSpawner {
    private static final String[] DERE_TYPES = {
            "NYANDERE",
            "HIMEDERE",
            "KUUDERE",
            "TSUNDERE",
            "YANDERE",
            "DEREDERE",
            "DANDERE"
    };
    private static final String[] ZODIAC_SIGNS = {
            "ARIES",
            "TAURUS",
            "GEMINI",
            "CANCER",
            "LEO",
            "VIRGO",
            "LIBRA",
            "SCORPIO",
            "SAGITTARIUS",
            "CAPRICORN",
            "AQUARIUS",
            "PISCES"
    };
    private static final Map<Long, Moe> RECENTLY_SPAWNED = new HashMap<>();

    private MoeSpawner() {
    }

    public static Moe create(ServerLevel level, BlockPos spawnPos, BlockState sourceState, UUID player) {
        return create(level, spawnPos, sourceState, player, new CompoundTag(), moe -> {
        });
    }

    public static Moe create(ServerLevel level, BlockPos spawnPos, BlockState sourceState, UUID player, Consumer<Moe> configure) {
        return create(level, spawnPos, sourceState, player, new CompoundTag(), configure);
    }

    public static Moe create(ServerLevel level, BlockPos spawnPos, BlockState sourceState, UUID player,
                             CompoundTag tileEntityData, Consumer<Moe> configure) {
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(spawnPos);
        applyRandomPersonality(moe, level.random);
        moe.setBlockState(sourceState);
        moe.setTileEntityData(tileEntityData);
        if (player != null) {
            moe.setPlayerUUID(player);
        }
        configure.accept(moe);
        if (!moe.hasHome()) {
            moe.setHasHome(true);
            moe.setHome(new DimBlockPos(level.dimension(), spawnPos));
        }
        Resolution resolution = resolve(level, moe, player);
        return resolution == null ? null : resolution.moe();
    }

    public static Moe spawn(ServerLevel level, BlockPos spawnPos, BlockState sourceState, UUID player,
                            CompoundTag tileEntityData, Consumer<Moe> configure) {
        Resolution resolution = createResolution(level, spawnPos, sourceState, player, tileEntityData, configure);
        if (resolution == null) {
            return null;
        }
        Moe moe = resolution.moe();
        if (isLoaded(level, moe)) {
            return moveLoadedTo(level, moe, spawnPos);
        }
        if (!level.addFreshEntity(moe)) {
            if (resolution.created()) {
                try {
                    BlockPartyDB.get(level).deleteNpc(moe.getDatabaseID());
                } catch (SQLException ignored) {
                    // Best-effort cleanup; the spawn still fails safely from the caller's perspective.
                }
            }
            return null;
        }
        RECENTLY_SPAWNED.put(moe.getDatabaseID(), moe);
        return moe;
    }

    private static Resolution createResolution(ServerLevel level, BlockPos spawnPos, BlockState sourceState, UUID player,
                                               CompoundTag tileEntityData, Consumer<Moe> configure) {
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(spawnPos);
        applyRandomPersonality(moe, level.random);
        moe.setBlockState(sourceState);
        moe.setTileEntityData(tileEntityData);
        if (player != null) {
            moe.setPlayerUUID(player);
        }
        configure.accept(moe);
        if (!moe.hasHome()) {
            moe.setHasHome(true);
            moe.setHome(new DimBlockPos(level.dimension(), spawnPos));
        }
        return resolve(level, moe, player);
    }

    public static void applyRandomPersonality(Moe moe, RandomSource random) {
        moe.setBloodType(weightedBloodType(random.nextInt(8)));
        moe.setDere(randomDere(random));
        moe.setZodiac(randomZodiac(random));
    }

    public static String randomDere(RandomSource random) {
        return DERE_TYPES[random.nextInt(DERE_TYPES.length)];
    }

    private static String randomZodiac(RandomSource random) {
        return ZODIAC_SIGNS[random.nextInt(ZODIAC_SIGNS.length)];
    }

    private static String weightedBloodType(int value) {
        if (value < 1) {
            return "AB";
        }
        if (value < 3) {
            return "B";
        }
        if (value < 5) {
            return "A";
        }
        return "O";
    }

    private static Resolution resolve(ServerLevel level, Moe moe, UUID player) {
        BlockPartyDB db = BlockPartyDB.get(level);
        NPC row;
        try {
            row = findExistingCardinalNpc(db, moe);
            if (row != null) {
                if (player != null) {
                    db.addPlayerNpc(player, row.databaseId());
                }
                Moe loaded = findLoaded(level, row.databaseId());
                if (loaded != null) {
                    return new Resolution(loaded, false);
                }
                row.applyTo(moe);
                return new Resolution(moe, false);
            }

            applyCardinalName(moe);
            row = db.createNpc(level, moe);
            row.applyTo(moe);
            if (player != null) {
                db.addPlayerNpc(player, row.databaseId());
            }
        } catch (SQLException exception) {
            return null;
        }
        return new Resolution(moe, true);
    }

    private static NPC findExistingCardinalNpc(BlockPartyDB db, Moe moe) throws SQLException {
        if (!moe.isCardinal()) {
            return null;
        }
        return db.findCardinalNpc(moe.getVisibleBlockState()).orElse(null);
    }

    private static void applyCardinalName(Moe moe) {
        if (!moe.isCardinal() || !"Tokumei".equals(moe.getGivenName())) {
            return;
        }
        String name = MoeFamilyNames.get(moe.getVisibleBlockState());
        if (!name.isBlank()) {
            moe.setGivenName(name);
        }
    }

    public static boolean isLoaded(ServerLevel level, Moe moe) {
        return findLoaded(level, moe.getDatabaseID()) == moe;
    }

    private static Moe moveLoadedTo(ServerLevel level, Moe moe, BlockPos spawnPos) {
        if (moe.level() == level) {
            moe.moveToBlock(spawnPos);
            return moe;
        }
        Entity teleported = moe.teleport(new TeleportTransition(
                level,
                Vec3.atBottomCenterOf(spawnPos),
                Vec3.ZERO,
                moe.getYRot(),
                moe.getXRot(),
                TeleportTransition.DO_NOTHING));
        return teleported instanceof Moe moved ? moved : moe;
    }

    private static Moe findLoaded(ServerLevel level, long databaseId) {
        Moe recent = RECENTLY_SPAWNED.get(databaseId);
        if (recent != null) {
            if (!recent.isRemoved()) {
                return recent;
            }
            RECENTLY_SPAWNED.remove(databaseId);
        }
        for (ServerLevel candidate : level.getServer().getAllLevels()) {
            Moe moe = CellPhone.findLoadedMoe(candidate, databaseId).orElse(null);
            if (moe != null) {
                RECENTLY_SPAWNED.put(databaseId, moe);
                return moe;
            }
        }
        return null;
    }

    private record Resolution(Moe moe, boolean created) {
    }
}
