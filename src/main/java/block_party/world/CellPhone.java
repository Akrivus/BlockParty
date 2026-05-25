package block_party.world;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.db.records.PlayerRelationship;
import block_party.entities.Moe;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.PlayerMovementContext;
import block_party.entities.movement.PlayerMovementDecision;
import block_party.entities.movement.PlayerMovementDecisions;
import block_party.entities.movement.PlayerMovementRequest;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcDetailPayload;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.SceneTrigger;
import block_party.scene.Speaker;
import block_party.world.chunk.ForcedChunk;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class CellPhone {
    private static final int MAX_PENDING_TICKS = 20;
    private static final List<PendingCall> PENDING_CALLS = new ArrayList<>();

    private final BlockPartyDB db;
    private final ServerLevel callerLevel;
    private final UUID player;
    private final Vec3 callerPos;
    private final float callerYRot;
    private final long npcId;
    private NPC npc;
    private PlayerRelationship relationship;
    private PlayerMovementDecision decision;
    private ServerLevel npcLevel;
    private CallFailure failure = CallFailure.UNREACHABLE;
    private boolean terminalFailure;

    public CellPhone(BlockPartyDB db, ServerLevel callerLevel, UUID player, Vec3 callerPos, float callerYRot, long npcId) {
        this.db = db;
        this.callerLevel = callerLevel;
        this.player = player;
        this.callerPos = callerPos;
        this.callerYRot = callerYRot;
        this.npcId = npcId;
    }

    public Optional<Moe> call() {
        if (!this.begin()) {
            return Optional.empty();
        }
        try {
            return this.tryComplete();
        } finally {
            this.release();
        }
    }

    public static void queue(BlockPartyDB db, ServerPlayer player, long npcId) {
        removePending(npcId);
        CellPhone call = new CellPhone(db, player.serverLevel(), player.getUUID(), player.position(), player.getYRot(), npcId);
        if (!call.begin()) {
            sendFailure(db, player, npcId, call.failure);
            return;
        }

        Optional<Moe> immediate = call.tryComplete();
        if (immediate.isPresent()) {
            call.release();
            send(player, npcId, immediate);
            return;
        }
        if (call.failed()) {
            call.release();
            sendFailure(db, player, npcId, call.failure);
            return;
        }

        PENDING_CALLS.add(new PendingCall(player, call, MAX_PENDING_TICKS));
    }

    private static void removePending(long npcId) {
        Iterator<PendingCall> iterator = PENDING_CALLS.iterator();
        while (iterator.hasNext()) {
            PendingCall pending = iterator.next();
            if (pending.call().npcId == npcId) {
                pending.call().release();
                iterator.remove();
            }
        }
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        Iterator<PendingCall> iterator = PENDING_CALLS.iterator();
        while (iterator.hasNext()) {
            PendingCall pending = iterator.next();
            if (pending.player().isRemoved() || pending.player().hasDisconnected()) {
                pending.call().release();
                iterator.remove();
                continue;
            }

            Optional<Moe> called = pending.call().tryComplete();
            if (called.isPresent()) {
                pending.call().release();
                send(pending.player(), pending.call().npcId, called);
                iterator.remove();
                continue;
            }
            if (pending.call().failed()) {
                pending.call().release();
                sendFailure(pending.call().db, pending.player(), pending.call().npcId, pending.call().failure);
                iterator.remove();
                continue;
            }

            if (pending.tick()) {
                pending.call().release();
                sendFailure(pending.call().db, pending.player(), pending.call().npcId, CallFailure.HIDING);
                iterator.remove();
            }
        }
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        PENDING_CALLS.forEach(pending -> pending.call().release());
        PENDING_CALLS.clear();
    }

    private static void send(ServerPlayer player, long npcId, Optional<Moe> moe) {
        PacketDistributor.sendToPlayer(player, NpcCallPayload.from(npcId, moe));
    }

    private boolean begin() {
        Optional<NPC> row = this.db.findNpcSafe(this.npcId);
        if (row.isEmpty()) {
            this.fail(CallFailure.NOT_IN_SERVICE);
            return false;
        }
        Optional<PlayerRelationship> foundRelationship = this.db.findPlayerRelationshipSafe(this.npcId, this.player);
        if (foundRelationship.isEmpty() || !foundRelationship.get().phoneContact()) {
            this.fail(CallFailure.ESTRANGED);
            return false;
        }
        if (row.get().dead()) {
            this.fail(CallFailure.DEAD);
            return false;
        }
        if (row.get().hiding()) {
            this.fail(CallFailure.HIDING);
            return false;
        }

        this.npc = row.get();
        this.relationship = foundRelationship.get();
        this.npcLevel = this.callerLevel.getServer().getLevel(this.npc.dimension());
        if (this.npcLevel == null) {
            this.fail(CallFailure.NOT_IN_SERVICE);
            return false;
        }

        PlayerMovementDecision movementDecision = this.decide(null);
        if (movementDecision.outcome() != PlayerMovementDecision.Outcome.ACCEPTED) {
            this.fail(CallFailure.from(movementDecision));
            return false;
        }
        this.decision = movementDecision;

        ForcedChunk.queue(this.npcId, this.npcLevel, new ChunkPos(this.npc.pos()));
        return true;
    }

    private Optional<Moe> tryComplete() {
        if (this.npc == null || this.npcLevel == null) {
            return Optional.empty();
        }
        Optional<Moe> live = findLoadedMoe(this.npcLevel, this.npcId);
        if (live.isEmpty()) {
            return Optional.empty();
        }
        PlayerMovementDecision movementDecision = this.decide(live.get());
        if (movementDecision.outcome() != PlayerMovementDecision.Outcome.ACCEPTED) {
            this.fail(CallFailure.from(movementDecision));
            return Optional.empty();
        }
        this.decision = movementDecision;
        return this.teleport(this.npc, live.get());
    }

    private void release() {
        ForcedChunk.release(this.npcId);
    }

    private PlayerMovementDecision decide(Moe loaded) {
        PlayerMovementRequest request = PlayerMovementRequest.phoneCall(
                this.player,
                this.npcId,
                this.callerLevel.dimension(),
                this.callerPos,
                this.callerYRot);
        return PlayerMovementDecisions.decide(PlayerMovementContext.from(request, this.npc, this.relationship, loaded));
    }

    private void fail(CallFailure failure) {
        this.failure = failure;
        this.terminalFailure = true;
    }

    private boolean failed() {
        return this.terminalFailure;
    }

    private static void sendFailure(BlockPartyDB db, ServerPlayer player, long npcId, CallFailure failure) {
        Optional<NPC> row = db.findNpcSafe(npcId);
        Optional<NPC> visibleRow = row.filter(npc -> db.hasPlayerRelationship(player.getUUID(), npc.databaseId()));
        Dialogue dialogue = new Dialogue(
                Component.translatable(failure.translationKey()).getString(),
                true,
                failure.speaker(visibleRow.isPresent()),
                BlockParty.source("item.cell_phone.dial"),
                Map.of(Response.NEXT_RESPONSE, Component.translatable("gui.block_party.call_scene.hang_up").getString()));
        PacketDistributor.sendToPlayer(player, new DialogueOpenPayload(NpcDetailPayload.from(db, player.getUUID(), npcId, visibleRow), dialogue));
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

        called.setDialogueTarget(this.player);
        called.startFollowSession(
                this.player,
                PlayerMovementIntent.PHONE_CALL,
                this.decision.followTicks(),
                this.decision.canChangeDimension());
        try {
            npc.updateFromMoe(this.db, this.callerLevel, called);
        } catch (SQLException exception) {
            return Optional.empty();
        }
        this.triggerCallScene(called);
        return Optional.of(called);
    }

    private void triggerCallScene(Moe called) {
        if (called.triggerScene(SceneTrigger.PHONE_CALL)) {
            return;
        }
        Dialogue dialogue = new Dialogue(
                Component.translatable("gui.block_party.call_scene.default").getString(),
                true,
                new Speaker(Speaker.Identity.CHARACTER, Speaker.Position.LEFT, "DEFAULT", "HAPPY", false, null, 1.0F),
                null,
                Map.of(Response.NEXT_RESPONSE, Component.translatable("gui.block_party.call_scene.hang_up").getString()));
        ServerPlayer player = this.callerLevel.getServer().getPlayerList().getPlayer(this.player);
        if (player != null) {
            PacketDistributor.sendToPlayer(player, DialogueOpenPayload.response(this.db, this.player, this.npcId, dialogue));
        }
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

    private static final class PendingCall {
        private final ServerPlayer player;
        private final CellPhone call;
        private int ticksRemaining;

        private PendingCall(ServerPlayer player, CellPhone call, int ticksRemaining) {
            this.player = player;
            this.call = call;
            this.ticksRemaining = ticksRemaining;
        }

        private ServerPlayer player() {
            return this.player;
        }

        private CellPhone call() {
            return this.call;
        }

        private boolean tick() {
            this.ticksRemaining--;
            return this.ticksRemaining <= 0;
        }
    }

    private enum CallFailure {
        NOT_IN_SERVICE("gui.block_party.call_result.not_in_service"),
        DEAD("gui.block_party.call_result.dead"),
        ESTRANGED("gui.block_party.call_result.estranged"),
        HIDING("gui.block_party.call_result.hiding"),
        VOICEMAIL("gui.block_party.call_result.voicemail"),
        BUSY("gui.block_party.call_result.busy"),
        UNREACHABLE("gui.block_party.call_result.unreachable");

        private final String translationKey;

        CallFailure(String translationKey) {
            this.translationKey = translationKey;
        }

        private String translationKey() {
            return this.translationKey;
        }

        private Speaker speaker(boolean hasContact) {
            if (!hasContact || this == NOT_IN_SERVICE || this == UNREACHABLE) {
                return new Speaker(Speaker.Identity.NARRATOR, Speaker.Position.CENTER, "DEFAULT", "NORMAL", false, null, 1.0F);
            }
            return new Speaker(Speaker.Identity.CHARACTER, Speaker.Position.LEFT, "DEFAULT", this == DEAD ? "SAD" : "NORMAL", false, null, 1.0F);
        }

        private static CallFailure from(PlayerMovementDecision decision) {
            if (decision.reason() == PlayerMovementDecision.Reason.BUSY) {
                return BUSY;
            }
            if (decision.outcome() == PlayerMovementDecision.Outcome.VOICEMAIL) {
                return VOICEMAIL;
            }
            return UNREACHABLE;
        }
    }
}
