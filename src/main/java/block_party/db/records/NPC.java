package block_party.db.records;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.sql.Column;
import block_party.db.sql.Row;
import block_party.db.sql.Table;
import block_party.entities.BlockPartyNPC;
import block_party.scene.filters.traits.BloodType;
import block_party.scene.filters.traits.Dere;
import block_party.registry.CustomEntities;
import block_party.world.chunk.ForcedChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class NPC extends Row<BlockPartyNPC> {
    public static final int DEAD = 6;
    public static final int NAME = 7;
    public static final int BLOOD_TYPE = 8;
    public static final int DERE = 9;
    public static final int HEALTH = 10;
    public static final int FOOD_LEVEL = 11;
    public static final int EXHAUSTION = 12;
    public static final int SATURATION = 13;
    public static final int STRESS = 14;
    public static final int RELAXATION = 15;
    public static final int LOYALTY = 16;
    public static final int AFFECTION = 17;
    public static final int SLOUCH = 18;
    public static final int AGE = 19;
    public static final int LAST_SEEN_AT = 20;
    public static final int BLOCK_STATE = 21;
    public static final int HIDING = 22;
    public static final int HAS_HOME = 23;
    public static final int HOME_POS = 24;
    public static final int HOME_POS_X = 25;
    public static final int HOME_POS_Y = 26;
    public static final int HOME_POS_Z = 27;
    public static final int SHRINE_GATE = 28;
    public static final int SHIMENAWA = 29;

    public NPC(ResultSet set) throws SQLException {
        super(BlockPartyDB.NPCs, set);
    }

    public NPC(CompoundTag compound) {
        super(BlockPartyDB.NPCs, compound);
    }

    public NPC(BlockPartyNPC entity) {
        super(BlockPartyDB.NPCs, entity);
    }

    @Override
    public void sync(BlockPartyNPC entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
        this.get(NAME).set(entity.getGivenName());
        this.get(BLOCK_STATE).set(entity.getActualBlockState());
        this.get(BLOOD_TYPE).set(entity.getBloodType());
        this.get(DERE).set(entity.getDere());
        this.get(HEALTH).set(entity.getHealth());
        this.get(FOOD_LEVEL).set(entity.getFoodLevel());
        this.get(EXHAUSTION).set(entity.getExhaustion());
        this.get(SATURATION).set(entity.getSaturation());
        this.get(STRESS).set(entity.getStress());
        this.get(RELAXATION).set(entity.getRelaxation());
        this.get(LOYALTY).set(entity.getLoyalty());
        this.get(AFFECTION).set(entity.getAffection());
        this.get(SLOUCH).set(entity.getSlouch());
        this.get(AGE).set(entity.getAge());
        this.get(LAST_SEEN_AT).set(entity.getLastSeen());
        this.get(HAS_HOME).set(entity.hasHome());
        this.get(HOME_POS).set(entity.getHome());
        this.get(DEAD).set(false);
    }

    public String getName() {
        return (String) this.get(NAME).get();
    }

    public boolean isDeadOrEstrangedFrom(Player player) {
        return this.isDead() || this.isEstrangedFrom(player);
    }

    public boolean isDead() {
        return (Boolean) this.get(DEAD).get();
    }

    public boolean isEstrangedFrom(Player player) {
        return !player.getUUID().equals(this.get(PLAYER_UUID).get());
    }

    public boolean isHiding() {
        return (Boolean) this.get(HIDING).get();
    }

    public BlockPartyNPC getServerEntity(MinecraftServer server) {
        DimBlockPos pos = (DimBlockPos) this.get(POS).get();
        ServerLevel level = ForcedChunk.queue(this.getID(), server.getLevel(pos.getDim()), pos.getChunk());
        if (level != null) {
            List<BlockPartyNPC> npcs = level.getEntitiesOfClass(BlockPartyNPC.class, pos.getAABB());
            for (BlockPartyNPC npc : npcs) {
                if (this.getID() == npc.getDatabaseID()) { return npc; }
            }
        }
        return null;
    }

    public BlockPartyNPC getClientEntity(Minecraft client) {
        BlockPos pos = client.player.blockPosition();
        BlockPartyNPC entity = CustomEntities.MOE.get().create(client.level);
        this.load(entity);
        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
        return entity;
    }

    @Override
    public void load(BlockPartyNPC entity) {
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
        entity.setGivenName((String) this.get(NAME).get());
        entity.setBlockState((BlockState) this.get(BLOCK_STATE).get());
        entity.setBloodType((BloodType) this.get(BLOOD_TYPE).get());
        entity.setDere((Dere) this.get(DERE).get());
        entity.setHealth((Float) this.get(HEALTH).get());
        entity.setFoodLevel((Float) this.get(FOOD_LEVEL).get());
        entity.setExhaustion((Float) this.get(EXHAUSTION).get());
        entity.setSaturation((Float) this.get(SATURATION).get());
        entity.setStress((Float) this.get(STRESS).get());
        entity.setRelaxation((Float) this.get(RELAXATION).get());
        entity.setLoyalty((Float) this.get(LOYALTY).get());
        entity.setAffection((Float) this.get(AFFECTION).get());
        entity.setAge((Float) this.get(AGE).get());
        entity.setLastSeen((Long) this.get(LAST_SEEN_AT).get());
        entity.setHasHome((Boolean) this.get(HAS_HOME).get());
        entity.setHome((DimBlockPos) this.get(HOME_POS).get());
        entity.setDatabaseID(this.getID());
    }

    public static NPC create(CompoundTag compound) {
        return new NPC(compound);
    }

    public static class Schema extends Table<NPC> {
        public Schema() {
            super("NPCs");
            this.addColumn(new Column.AsBoolean(this, "Dead"));
            this.addColumn(new Column.AsString(this, "Name"));
            this.addColumn(new Column.AsTrait<>(this, "BloodType", BloodType.O));
            this.addColumn(new Column.AsTrait<>(this, "Dere", Dere.NYANDERE));
            this.addColumn(new Column.AsFloat(this, "Health"));
            this.addColumn(new Column.AsFloat(this, "FoodLevel"));
            this.addColumn(new Column.AsFloat(this, "Exhaustion"));
            this.addColumn(new Column.AsFloat(this, "Saturation"));
            this.addColumn(new Column.AsFloat(this, "Stress"));
            this.addColumn(new Column.AsFloat(this, "Relaxation"));
            this.addColumn(new Column.AsFloat(this, "Loyalty"));
            this.addColumn(new Column.AsFloat(this, "Affection"));
            this.addColumn(new Column.AsFloat(this, "Slouch"));
            this.addColumn(new Column.AsFloat(this, "Age"));
            this.addColumn(new Column.AsLong(this, "LastSeenAt"));
            this.addColumn(new Column.AsBlockState(this, "BlockState"));
            this.addColumn(new Column.AsBoolean(this, "Hiding"));
            this.addColumn(new Column.AsBoolean(this, "HasHome"));
            this.addColumn(new Column.AsPosition(this, "HomePos"));
            this.addColumn(new Column.AsReference<>(this, "Shrine", (uuid) -> BlockPartyDB.Shrines.find(uuid)));
        }

        @Override
        public NPC getRow(ResultSet set) throws SQLException {
            return new NPC(set);
        }
    }
}
