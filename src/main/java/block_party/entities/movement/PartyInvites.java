package block_party.entities.movement;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.db.records.PlayerRelationship;
import block_party.entities.Moe;
import block_party.network.payload.DialogueOpenPayload;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.SceneTrigger;
import block_party.scene.Speaker;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PartyInvites {
    private PartyInvites() {
    }

    public static InteractionResult request(Player player, Moe moe) {
        if (player == null || moe == null || moe.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(moe.level() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }
        BlockPartyDB db = BlockPartyDB.get(level);
        Optional<NPC> row = db.findNpcSafe(moe.getDatabaseID());
        Optional<PlayerRelationship> relationship = db.findPlayerRelationshipSafe(moe.getDatabaseID(), player.getUUID());
        if (row.isEmpty() || relationship.isEmpty()) {
            openResult(db, player, moe, InviteResult.MISSING_RELATIONSHIP);
            return InteractionResult.SUCCESS;
        }

        PlayerMovementRequest request = PlayerMovementRequest.partyInvite(
                player.getUUID(),
                moe.getDatabaseID(),
                level.dimension(),
                player.position(),
                player.getYRot());
        PlayerMovementDecision decision = PlayerMovementDecisions.decide(PlayerMovementContext.from(request, row.get(), relationship.get(), moe));
        if (decision.outcome() == PlayerMovementDecision.Outcome.ACCEPTED) {
            moe.setDialogueTarget(player.getUUID());
            moe.startFollowSession(player.getUUID(), PlayerMovementIntent.PARTY_INVITE, decision.followTicks(), decision.canChangeDimension(), false);
            if (!moe.triggerScene(SceneTrigger.PARTY_INVITE)) {
                openResult(db, player, moe, InviteResult.ACCEPTED);
            }
            return InteractionResult.SUCCESS;
        }

        openResult(db, player, moe, InviteResult.from(decision.reason()));
        return InteractionResult.SUCCESS;
    }

    private static void openResult(BlockPartyDB db, Player player, Moe moe, InviteResult result) {
        Dialogue dialogue = new Dialogue(
                Component.translatable(result.translationKey()).getString(),
                true,
                new Speaker(Speaker.Identity.CHARACTER, Speaker.Position.LEFT, "DEFAULT", result.emotion(), false, null, 1.0F),
                BlockParty.source("moe.follow"),
                Map.of(Response.NEXT_RESPONSE, Component.translatable("gui.block_party.call_scene.hang_up").getString()));
        moe.setDialogue(dialogue);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, DialogueOpenPayload.response(db, player.getUUID(), moe.getDatabaseID(), dialogue));
        }
    }

    private enum InviteResult {
        ACCEPTED("gui.block_party.party_invite.accepted", "HAPPY"),
        MISSING_RELATIONSHIP("gui.block_party.party_invite.missing_relationship", "NORMAL"),
        LOW_LOYALTY("gui.block_party.party_invite.low_loyalty", "NORMAL"),
        TOO_STRESSED("gui.block_party.party_invite.too_stressed", "TIRED"),
        DIFFERENT_DIMENSION("gui.block_party.party_invite.different_dimension", "NORMAL"),
        BUSY("gui.block_party.party_invite.busy", "NORMAL"),
        UNREACHABLE("gui.block_party.party_invite.unreachable", "NORMAL");

        private final String translationKey;
        private final String emotion;

        InviteResult(String translationKey, String emotion) {
            this.translationKey = translationKey;
            this.emotion = emotion;
        }

        private String translationKey() {
            return this.translationKey;
        }

        private String emotion() {
            return this.emotion;
        }

        private static InviteResult from(PlayerMovementDecision.Reason reason) {
            return switch (reason) {
                case MISSING_RELATIONSHIP -> MISSING_RELATIONSHIP;
                case LOW_LOYALTY -> LOW_LOYALTY;
                case TOO_STRESSED -> TOO_STRESSED;
                case DIFFERENT_DIMENSION -> DIFFERENT_DIMENSION;
                case BUSY -> BUSY;
                default -> UNREACHABLE;
            };
        }
    }
}
