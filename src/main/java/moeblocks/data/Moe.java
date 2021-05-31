package moeblocks.data;

import moeblocks.automata.trait.BloodType;
import moeblocks.automata.trait.Dere;
import moeblocks.data.sql.Column;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeEntities;
import moeblocks.init.MoeWorldData;
import moeblocks.util.ChunkScheduler;
import moeblocks.util.DimBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Moe extends AbstractNPC<MoeEntity> {
    protected static final int BLOCK_STATE  = 21;
    protected static final int TORII_GATE   = 22;
    protected static final int SHIMENAWA    = 23;

    public Moe(ResultSet set) throws SQLException {
        super(MoeWorldData.Moes, set);
    }

    public Moe(CompoundNBT compound) {
        super(MoeWorldData.Moes, compound);
    }

    public Moe(MoeEntity entity) {
        super(MoeWorldData.Moes, entity);
    }

    @Override
    public void sync(MoeEntity entity) {
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
    public void load(MoeEntity entity) {
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

    @Override
    public MoeEntity getServerEntity(MinecraftServer server) {
        DimBlockPos pos = (DimBlockPos) this.get(POS).get();
        ServerWorld world = ChunkScheduler.queue(this.getID(), server.getWorld(pos.getDim()), pos.getChunk());
        if (world != null) {
            List<MoeEntity> npcs = world.getEntitiesWithinAABB(MoeEntity.class, pos.getAABB());
            for (MoeEntity npc : npcs) {
                if (this.getID().equals(npc.getDatabaseID())) { return npc; }
            }
        }
        return null;
    }

    @Override
    public MoeEntity getClientEntity(Minecraft client) {
        BlockPos pos = client.player.getPosition();
        MoeEntity entity = MoeEntities.MOE.get().create(client.world);
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        this.load(entity);
        return entity;
    }

    public static class Schema extends AbstractNPC.Schema<Moe> {
        public Schema() {
            super("Moes");
            this.addColumn(new Column.AsBlockState(this, "BlockState"));
            this.addColumn(new Column.AsReference<>(this, "ToriiGate", (uuid) -> MoeWorldData.ToriiGates.find(uuid)));
            this.addColumn(new Column.AsReference<>(this, "Shimenawa", (uuid) -> MoeWorldData.Shimenawa.find(uuid)));
        }

        @Override
        public Moe getRow(ResultSet set) throws SQLException {
            return new Moe(set);
        }
    }
}
