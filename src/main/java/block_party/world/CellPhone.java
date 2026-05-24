package block_party.world;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.world.chunk.ForcedChunk;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public final class CellPhone {
    private final BlockPartyDB db;
    private final ServerLevel callerLevel;
    private final UUID player;
    private final Vec3 callerPos;
    private final float callerYRot;
    private final long npcId;

    public CellPhone(BlockPartyDB db, ServerLevel callerLevel, UUID player, Vec3 callerPos, float callerYRot, long npcId) {
        this.db = db;
        this.callerLevel = callerLevel;
        this.player = player;
        this.callerPos = callerPos;
        this.callerYRot = callerYRot;
        this.npcId = npcId;
    }

    public Optional<Moe> call() {
        Optional<NPC> row = this.db.loadOwnedNpc(this.player, this.npcId);
        if (row.isEmpty() || row.get().hiding()) {
            return Optional.empty();
        }

        NPC npc = row.get();
        ServerLevel npcLevel = this.callerLevel.getServer().getLevel(npc.dimension());
        if (npcLevel == null) {
            return Optional.empty();
        }

        ForcedChunk.queue(this.npcId, npcLevel, new ChunkPos(npc.pos()));
        try {
            Optional<Moe> live = findLoadedMoe(npcLevel, this.npcId);
            if (live.isEmpty()) {
                return Optional.empty();
            }
            return this.teleport(npc, live.get());
        } finally {
            ForcedChunk.release(this.npcId);
        }
    }

    private Optional<Moe> teleport(NPC npc, Moe moe) {
        Vec3 arrival = this.arrivalPosition();
        Moe called = moe;
        if (moe.level() == this.callerLevel) {
            moe.absMoveTo(arrival.x, arrival.y, arrival.z, moe.getYRot(), moe.getXRot());
        } else {
            Entity teleported = moe.teleport(new TeleportTransition(
                    this.callerLevel,
                    arrival,
                    Vec3.ZERO,
                    moe.getYRot(),
                    moe.getXRot(),
                    TeleportTransition.DO_NOTHING));
            if (!(teleported instanceof Moe changed)) {
                return Optional.empty();
            }
            called = changed;
        }

        called.setFollowing(true);
        try {
            npc.updateFromMoe(this.db, this.callerLevel, called);
        } catch (SQLException exception) {
            return Optional.empty();
        }
        return Optional.of(called);
    }

    private Vec3 arrivalPosition() {
        return new Vec3(
                this.callerPos.x - Math.sin(Math.toRadians(this.callerYRot)) * 1.44D,
                this.callerPos.y,
                this.callerPos.z + Math.cos(Math.toRadians(this.callerYRot)) * 1.44D);
    }

    public static Optional<Moe> findLoadedMoe(ServerLevel level, long id) {
        for (Moe moe : level.getEntities(EntityTypeTest.forClass(Moe.class), moe ->
                moe.isAlive() && !moe.isRemoved() && moe.getDatabaseID() == id)) {
            return Optional.of(moe);
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Moe moe && moe.isAlive() && !moe.isRemoved() && moe.getDatabaseID() == id) {
                return Optional.of(moe);
            }
        }
        return Optional.empty();
    }
}
