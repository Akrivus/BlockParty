package block_party.entities.movement;

import block_party.db.records.NPC;
import block_party.db.records.PlayerRelationship;
import block_party.entities.Moe;
import net.minecraft.world.phys.Vec3;

public record PlayerMovementContext(
        PlayerMovementRequest request,
        boolean relationshipPresent,
        boolean yearbookSigned,
        boolean phoneContact,
        boolean sameDimension,
        double distanceSqr,
        float affection,
        float loyalty,
        float stress,
        float relaxation,
        boolean following,
        int followTicksRemaining,
        boolean followCanChangeDimension,
        boolean sitting,
        boolean passenger) {
    public static PlayerMovementContext from(PlayerMovementRequest request, NPC row, PlayerRelationship relationship, Moe loaded) {
        boolean sameDimension = row.dimension().equals(request.dimension());
        double distanceSqr = sameDimension ? Vec3.atBottomCenterOf(row.pos()).distanceToSqr(request.position()) : Double.POSITIVE_INFINITY;
        float stress = loaded == null ? row.stress() : loaded.getStress();
        float relaxation = loaded == null ? row.relaxation() : loaded.getRelaxation();
        boolean following = loaded != null && loaded.isFollowing();
        int followTicksRemaining = loaded == null ? 0 : loaded.getFollowTicksRemaining();
        boolean followCanChangeDimension = loaded != null && loaded.canFollowAcrossDimensions();
        boolean sitting = loaded != null && loaded.isSitting();
        boolean passenger = loaded != null && loaded.isPassenger();
        return new PlayerMovementContext(
                request,
                true,
                relationship.yearbookSigned(),
                relationship.phoneContact(),
                sameDimension,
                distanceSqr,
                relationship.affection(),
                relationship.loyalty(),
                stress,
                relaxation,
                following,
                followTicksRemaining,
                followCanChangeDimension,
                sitting,
                passenger);
    }
}
