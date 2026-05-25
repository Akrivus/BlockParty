package block_party.entities.movement;

public final class PlayerMovementDecisions {
    private static final float PHONE_LOYALTY = 6.0F;
    private static final float FOLLOW_LOYALTY = 8.0F;
    private static final float PARTY_LOYALTY = 10.0F;
    private static final float DIMENSION_LOYALTY = 16.0F;
    private static final float STRESSED = 18.0F;
    private static final double FAR_SAME_DIMENSION_DISTANCE_SQR = 96.0D * 96.0D;

    private PlayerMovementDecisions() {
    }

    public static PlayerMovementDecision decide(PlayerMovementContext context) {
        if (!context.relationshipPresent()) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.MISSING_RELATIONSHIP);
        }
        if (context.passenger()) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.BUSY);
        }
        return switch (context.request().intent()) {
            case PHONE_CALL -> phoneCall(context);
            case PARTY_INVITE -> loyalFollow(context, PARTY_LOYALTY);
            case FOLLOW_REQUEST -> loyalFollow(context, FOLLOW_LOYALTY);
            case COME_HERE -> comeHere(context);
            case WAIT, DISMISS -> PlayerMovementDecision.accept(0, false);
        };
    }

    private static PlayerMovementDecision phoneCall(PlayerMovementContext context) {
        if (!context.phoneContact()) {
            return PlayerMovementDecision.voicemail(PlayerMovementDecision.Reason.NO_PHONE_CONTACT);
        }
        if (context.stress() >= STRESSED && context.loyalty() < DIMENSION_LOYALTY) {
            return PlayerMovementDecision.voicemail(PlayerMovementDecision.Reason.TOO_STRESSED);
        }
        if (!context.sameDimension() && context.loyalty() < DIMENSION_LOYALTY) {
            return PlayerMovementDecision.voicemail(PlayerMovementDecision.Reason.DIFFERENT_DIMENSION);
        }
        if (context.sameDimension() && context.distanceSqr() > FAR_SAME_DIMENSION_DISTANCE_SQR && context.loyalty() < FOLLOW_LOYALTY) {
            return PlayerMovementDecision.voicemail(PlayerMovementDecision.Reason.TOO_FAR);
        }
        if (context.loyalty() < PHONE_LOYALTY) {
            return PlayerMovementDecision.voicemail(PlayerMovementDecision.Reason.LOW_LOYALTY);
        }
        return PlayerMovementDecision.accept(followTicks(context), !context.sameDimension());
    }

    private static PlayerMovementDecision loyalFollow(PlayerMovementContext context, float requiredLoyalty) {
        if (context.sitting()) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.BUSY);
        }
        if (context.stress() >= STRESSED && context.loyalty() < DIMENSION_LOYALTY) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.TOO_STRESSED);
        }
        if (!context.sameDimension() && context.loyalty() < DIMENSION_LOYALTY) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.DIFFERENT_DIMENSION);
        }
        if (context.loyalty() < requiredLoyalty) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.LOW_LOYALTY);
        }
        return PlayerMovementDecision.accept(followTicks(context), !context.sameDimension());
    }

    private static PlayerMovementDecision comeHere(PlayerMovementContext context) {
        if (!context.sameDimension()) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.DIFFERENT_DIMENSION);
        }
        if (context.loyalty() < PHONE_LOYALTY) {
            return PlayerMovementDecision.refuse(PlayerMovementDecision.Reason.LOW_LOYALTY);
        }
        return PlayerMovementDecision.accept(followTicks(context) / 2, false);
    }

    private static int followTicks(PlayerMovementContext context) {
        float seconds = 20.0F
                + context.loyalty() * 8.0F
                + context.affection() * 2.0F
                + context.relaxation()
                - context.stress() * 1.5F;
        return Math.max(20 * 10, Math.round(seconds) * 20);
    }
}
