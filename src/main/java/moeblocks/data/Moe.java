package moeblocks.data;

import moeblocks.data.sql.Column;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeData;
import moeblocks.init.MoeEntities;
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

public class Moe extends AbstractNPC<MoeEntity> {
    protected static final int BLOCK_STATE  = 21;
    protected static final int TORII_GATE   = 22;
    protected static final int SHIMENAWA    = 23;

    public Moe(ResultSet set) throws SQLException {
        super(MoeData.Moes, set);
    }

    public Moe(CompoundNBT compound) {
        super(MoeData.Moes, compound);
    }

    public Moe(MoeEntity entity) {
        super(MoeData.Moes, entity);
        this.get(BLOCK_STATE).setValue(entity.getInternalBlockState());
    }

    @Override
    public void sync(MoeEntity entity) {
        super.sync(entity);
        this.get(BLOCK_STATE).setValue(entity.getBlockState());
    }

    @Override
    public void load(MoeEntity entity) {
        super.load(entity);
        entity.setBlockState((BlockState) this.get(BLOCK_STATE).get());
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
        return null;
    }

    public static class Schema extends AbstractNPC.Schema<Moe> {
        public Schema() {
            super("Moes");
            this.addColumn(new Column.AsBlockState(this, "BlockState"));
            this.addColumn(new Column.AsReference<>(this, "ToriiGate", (uuid) -> MoeData.ToriiGates.find(uuid)));
            this.addColumn(new Column.AsReference<>(this, "Shimenawa", (uuid) -> MoeData.Shimenawa.find(uuid)));
        }

        @Override
        public Moe getRow(ResultSet set) throws SQLException {
            return new Moe(set);
        }
    }
}
