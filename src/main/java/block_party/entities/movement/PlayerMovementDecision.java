package block_party.entities.movement;

public record PlayerMovementDecision(
        Outcome outcome,
        Reason reason,
        int followTicks,
        boolean canChangeDimension) {
    public static PlayerMovementDecision accept(int followTicks, boolean canChangeDimension) {
        return new PlayerMovementDecision(Outcome.ACCEPTED, Reason.NONE, Math.max(0, followTicks), canChangeDimension);
    }

    public static PlayerMovementDecision refuse(Reason reason) {
        return new PlayerMovementDecision(Outcome.REFUSED, reason, 0, false);
    }

    public static PlayerMovementDecision voicemail(Reason reason) {
        return new PlayerMovementDecision(Outcome.VOICEMAIL, reason, 0, false);
    }

    public enum Outcome {
        ACCEPTED,
        REFUSED,
        VOICEMAIL,
        DELAYED,
        IGNORED
    }

    public enum Reason {
        NONE,
        MISSING_RELATIONSHIP,
        NO_PHONE_CONTACT,
        LOW_LOYALTY,
        TOO_STRESSED,
        TOO_FAR,
        DIFFERENT_DIMENSION,
        BUSY,
        HIDING,
        DEAD,
        UNREACHABLE
    }
}
