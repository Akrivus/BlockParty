package block_party.db.records;

import block_party.db.BlockPartyDB;
import block_party.db.sql.Column;
import block_party.db.sql.Row;
import block_party.db.sql.Table;
import block_party.custom.CustomEntities;
import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.trait.BloodType;
import block_party.npc.automata.trait.Dere;
import block_party.world.chunk.ChunkScheduler;
import block_party.db.DimBlockPos;
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
    protected static final int DEAD         =  6;
    protected static final int NAME         =  7;
    protected static final int BLOOD_TYPE   =  8;
    protected static final int DERE         =  9;
    protected static final int HEALTH       = 10;
    protected static final int FULLNESS     = 11;
    protected static final int EXHAUSTION   = 12;
    protected static final int SATURATION   = 13;
    protected static final int STRESS       = 14;
    protected static final int RELAXATION   = 15;
    protected static final int LOYALTY      = 16;
    protected static final int AFFECTION    = 17;
    protected static final int SLOUCH       = 18;
    protected static final int AGE          = 19;
    protected static final int LAST_SEEN_AT = 20;
    protected static final int BLOCK_STATE  = 21;
    protected static final int SHRINE_GATE   = 22;
    protected static final int SHIMENAWA    = 23;

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
        this.get(BLOCK_STATE).set(entity.getInternalBlockState());
        this.get(BLOOD_TYPE).set(entity.getBloodType());
        this.get(DERE).set(entity.getDere());
        this.get(HEALTH).set(entity.getHealth());
        this.get(FULLNESS).set(entity.getFullness());
        this.get(EXHAUSTION).set(entity.getExhaustion());
        this.get(SATURATION).set(entity.getSaturation());
        this.get(STRESS).set(entity.getStress());
        this.get(RELAXATION).set(entity.getRelaxation());
        this.get(LOYALTY).set(entity.getLoyalty());
        this.get(AFFECTION).set(entity.getAffection());
        this.get(SLOUCH).set(entity.getSlouch());
        this.get(AGE).set(entity.getAge());
        this.get(LAST_SEEN_AT).set(entity.getLastSeen());
        this.get(DEAD).set(false);
    }

    @Override
    public void load(BlockPartyNPC entity) {
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
        entity.setGivenName((String) this.get(NAME).get());
        entity.setBlockState((BlockState) this.get(BLOCK_STATE).get());
        entity.setBloodType((BloodType) this.get(BLOOD_TYPE).get());
        entity.setDere((Dere) this.get(DERE).get());
        entity.setHealth((Float) this.get(HEALTH).get());
        entity.setFullness((Float) this.get(FULLNESS).get());
        entity.setExhaustion((Float) this.get(EXHAUSTION).get());
        entity.setSaturation((Float) this.get(SATURATION).get());
        entity.setStress((Float) this.get(STRESS).get());
        entity.setRelaxation((Float) this.get(RELAXATION).get());
        entity.setLoyalty((Float) this.get(LOYALTY).get());
        entity.setAffection((Float) this.get(AFFECTION).get());
        entity.setAge((Float) this.get(AGE).get());
        entity.setLastSeen((Long) this.get(LAST_SEEN_AT).get());
    }

    public String getName() {
        return (String) this.get(NAME).get();
    }

    public boolean isDead() {
        return (Boolean) this.get(DEAD).get();
    }

    public boolean isEstrangedFrom(Player player) {
        return !player.getUUID().equals(this.get(PLAYER_UUID).get());
    }

    public boolean isDeadOrEstrangedFrom(Player player) {
        return this.isDead() || this.isEstrangedFrom(player);
    }

    public BlockPartyNPC getServerEntity(MinecraftServer server) {
        DimBlockPos pos = (DimBlockPos) this.get(POS).get();
        ServerLevel level = ChunkScheduler.queue(this.getID(), server.getLevel(pos.getDim()), pos.getChunk());
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
        BlockPartyNPC entity = CustomEntities.NPC.get().create(client.level);
        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
        this.load(entity);
        return entity;
    }

    public static NPC create(CompoundTag compound) {
        return new NPC(compound);
    }

    public static class Schema extends Table<NPC> {
        public Schema() {
            super("BlockPartyNPCs");
            this.addColumn(new Column.AsBoolean(this, "Dead"));
            this.addColumn(new Column.AsString(this, "Name"));
            this.addColumn(new Column.AsTrait<>(this, "BloodType", BloodType.O));
            this.addColumn(new Column.AsTrait<>(this, "Dere", Dere.NYANDERE));
            this.addColumn(new Column.AsFloat(this, "Health"));
            this.addColumn(new Column.AsFloat(this, "Fullness"));
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
            this.addColumn(new Column.AsReference<>(this, "Shrine", (uuid) -> BlockPartyDB.Shrines.find(uuid)));
        }

        @Override
        public NPC getRow(ResultSet set) throws SQLException {
            return new NPC(set);
        }
    }
}
