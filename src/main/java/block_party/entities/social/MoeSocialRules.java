package block_party.entities.social;

import java.util.Locale;

public final class MoeSocialRules {
    private MoeSocialRules() {
    }

    public static SocialSignal bloodSignal(String observerBloodType, String targetBloodType) {
        String observer = normalizeBloodType(observerBloodType);
        String target = normalizeBloodType(targetBloodType);

        float affinity = 0.1F;
        float tension = 0.0F;
        float interest = 0.1F;

        if (observer.equals(target)) {
            affinity += 0.35F;
            interest += 0.2F;
        }
        if (canDonateTo(observer, target)) {
            affinity += 0.3F;
            interest += 0.25F;
        }
        if ("O".equals(observer)) {
            interest += 0.2F;
        }
        if ("AB".equals(target)) {
            affinity += 0.2F;
            interest += 0.3F;
        }
        if (isUneasyPair(observer, target)) {
            tension += 0.2F;
            interest -= 0.1F;
        }

        return new SocialSignal(clamp(affinity), clamp(tension), clamp(interest));
    }

    public static SocialSignal combine(SocialSignal first, SocialSignal second) {
        return new SocialSignal(
                clamp(first.affinity() + second.affinity()),
                clamp(first.tension() + second.tension()),
                clamp(first.interest() + second.interest()));
    }

    public static SocialMovement movementFor(SocialSignal signal, double distanceSqr) {
        if (signal.tension() > signal.affinity() && distanceSqr < 16.0D) {
            return SocialMovement.AVOID;
        }
        if (signal.affinity() >= 0.55F && distanceSqr > 2.25D) {
            return SocialMovement.APPROACH;
        }
        return SocialMovement.IDLE;
    }

    public static SocialPlaceBehavior placeBehavior(String dereType, String bloodType, SocialSignal signal, int occupancy, int capacity) {
        String dere = normalizeDere(dereType);
        String blood = normalizeBloodType(bloodType);
        boolean crowded = capacity > 0 && occupancy >= capacity;
        if (signal.tension() > signal.affinity() || crowded && signal.tension() >= 0.2F) {
            return switch (dere) {
                case "YANDERE", "HIMEDERE" -> SocialPlaceBehavior.GUARD;
                default -> SocialPlaceBehavior.AVOID;
            };
        }
        if (signal.affinity() >= 0.6F) {
            return switch (dere) {
                case "YANDERE", "HIMEDERE" -> SocialPlaceBehavior.GUARD;
                case "KUUDERE", "DANDERE" -> SocialPlaceBehavior.ORBIT;
                default -> SocialPlaceBehavior.SHARE;
            };
        }
        if (signal.interest() >= 0.5F || "O".equals(blood) || "AB".equals(blood)) {
            return SocialPlaceBehavior.ORBIT;
        }
        return SocialPlaceBehavior.IGNORE;
    }

    public static int socialTickDelay(String dereType, int randomValue) {
        float activity = dereActivity(dereType);
        int base = Math.max(16, Math.round(30.0F / activity));
        int range = Math.max(16, Math.round(40.0F / activity));
        return base + Math.floorMod(randomValue, range);
    }

    public static double socialStepDistance(String dereType) {
        return 5.0D * dereActivity(dereType);
    }

    public static double socialMoveSpeed(String dereType) {
        return 0.85D + dereActivity(dereType) * 0.2D;
    }

    public static int socialMovementDuration(String dereType) {
        return Math.max(14, Math.round(28.0F / dereActivity(dereType)));
    }

    public static String compatibleEmotion(String dereType) {
        return switch (normalizeDere(dereType)) {
            case "DANDERE", "TSUNDERE" -> "EMBARRASSED";
            case "HIMEDERE" -> "SNOOTY";
            case "YANDERE" -> "SMITTEN";
            default -> "HAPPY";
        };
    }

    public static String tenseEmotion(String dereType) {
        return switch (normalizeDere(dereType)) {
            case "DANDERE" -> "SCARED";
            case "HIMEDERE" -> "SNOOTY";
            case "TSUNDERE" -> "ANGRY";
            case "YANDERE" -> "PSYCHOTIC";
            default -> "CONFUSED";
        };
    }

    public static SocialVisual visualFor(String observerBloodType, String targetBloodType, SocialSignal signal) {
        if (signal.tension() > signal.affinity()) {
            return SocialVisual.TENSION;
        }
        if (signal.affinity() >= 0.6F && "AB".equals(normalizeBloodType(targetBloodType))) {
            return SocialVisual.FAME;
        }
        if (signal.affinity() >= 0.6F) {
            return SocialVisual.AFFINITY;
        }
        if (signal.interest() >= 0.5F) {
            return SocialVisual.INTEREST;
        }
        return SocialVisual.NONE;
    }

    public static DereReaction dereReaction(String dereType, SocialVisual visual, SocialSignal signal, double distanceSqr) {
        if (visual == SocialVisual.NONE) {
            return DereReaction.NONE;
        }
        String dere = normalizeDere(dereType);
        if (signal.tension() > signal.affinity()) {
            return switch (dere) {
                case "YANDERE" -> DereReaction.CLING;
                case "TSUNDERE" -> DereReaction.FLUSTER_RETREAT;
                case "DANDERE" -> DereReaction.SHY_RETREAT;
                case "HIMEDERE" -> DereReaction.SHOW_OFF;
                case "KUUDERE" -> DereReaction.OBSERVE;
                default -> DereReaction.NONE;
            };
        }
        return switch (dere) {
            case "DEREDERE", "NYANDERE" -> DereReaction.CELEBRATE;
            case "YANDERE" -> DereReaction.CLING;
            case "TSUNDERE" -> distanceSqr < 16.0D ? DereReaction.FLUSTER_RETREAT : DereReaction.OBSERVE;
            case "DANDERE" -> distanceSqr < 9.0D ? DereReaction.SHY_RETREAT : DereReaction.OBSERVE;
            case "HIMEDERE" -> visual == SocialVisual.FAME ? DereReaction.SHOW_OFF : DereReaction.OBSERVE;
            case "KUUDERE" -> DereReaction.OBSERVE;
            default -> DereReaction.NONE;
        };
    }

    public static String reactionEmotion(String dereType, DereReaction reaction, boolean tense) {
        return switch (reaction) {
            case CELEBRATE -> "HAPPY";
            case CLING -> tense ? "PSYCHOTIC" : "SMITTEN";
            case FLUSTER_RETREAT -> tense ? "ANGRY" : "EMBARRASSED";
            case SHY_RETREAT -> tense ? "SCARED" : "EMBARRASSED";
            case SHOW_OFF -> "SNOOTY";
            case OBSERVE -> "KUUDERE".equals(normalizeDere(dereType)) ? "NORMAL" : compatibleEmotion(dereType);
            case NONE -> tense ? tenseEmotion(dereType) : compatibleEmotion(dereType);
        };
    }

    public static String responseEmotion(String dereType, SocialSignal signal, DereReaction reaction, String targetEmotion) {
        String dere = normalizeDere(dereType);
        String observed = normalizeEmotion(targetEmotion);
        boolean highAffinity = signal.affinity() >= 0.6F;
        boolean highTension = signal.tension() >= 0.45F || signal.tension() > signal.affinity();
        boolean highInterest = signal.interest() >= 0.5F;
        boolean mixed = signal.affinity() >= 0.35F && signal.tension() >= 0.35F && highInterest;

        if ("KUUDERE".equals(dere)) {
            return highTension && highInterest ? "CONFUSED" : "NORMAL";
        }
        if ("YANDERE".equals(dere)) {
            if ("SMITTEN".equals(observed) || highAffinity && highInterest) {
                return highTension ? "PSYCHOTIC" : "SMITTEN";
            }
            if (highTension) {
                return highInterest ? "PSYCHOTIC" : "ANGRY";
            }
        }
        if ("DANDERE".equals(dere) && (highTension || mixed || highInterest && !highAffinity)) {
            return highTension ? "SCARED" : "EMBARRASSED";
        }
        if ("TSUNDERE".equals(dere) && (mixed || highTension)) {
            return mixed || highAffinity ? "EMBARRASSED" : "ANGRY";
        }
        if ("HIMEDERE".equals(dere) && highInterest) {
            return highTension && !highAffinity ? "ANGRY" : "SNOOTY";
        }
        if ("NYANDERE".equals(dere) && highAffinity) {
            return "HAPPY";
        }

        if (highTension && highInterest) {
            return "CONFUSED";
        }
        if (highTension) {
            return tenseEmotion(dere);
        }
        if (highAffinity && highInterest) {
            return "SMITTEN";
        }
        if (highAffinity) {
            return compatibleEmotion(dere);
        }
        if (highInterest) {
            return switch (reaction) {
                case SHOW_OFF -> "SNOOTY";
                case FLUSTER_RETREAT, SHY_RETREAT -> "EMBARRASSED";
                default -> "CONFUSED";
            };
        }
        return "NORMAL";
    }

    public static boolean canDonateTo(String donorBloodType, String receiverBloodType) {
        String donor = normalizeBloodType(donorBloodType);
        String receiver = normalizeBloodType(receiverBloodType);
        return switch (donor) {
            case "O" -> true;
            case "A" -> "A".equals(receiver) || "AB".equals(receiver);
            case "B" -> "B".equals(receiver) || "AB".equals(receiver);
            case "AB" -> "AB".equals(receiver);
            default -> false;
        };
    }

    public static String normalizeBloodType(String value) {
        if (value == null || value.isBlank()) {
            return "O";
        }
        String normalized = value.toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "A", "AB", "B", "O" -> normalized;
            default -> "O";
        };
    }

    public static String normalizeDere(String value) {
        if (value == null || value.isBlank()) {
            return "NYANDERE";
        }
        String normalized = value.toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "NYANDERE", "HIMEDERE", "KUUDERE", "TSUNDERE", "YANDERE", "DEREDERE", "DANDERE" -> normalized;
            default -> "NYANDERE";
        };
    }

    public static String normalizeEmotion(String value) {
        if (value == null || value.isBlank()) {
            return "NORMAL";
        }
        String normalized = value.toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "ANGRY", "BEGGING", "CONFUSED", "CRYING", "MISCHIEVOUS", "EMBARRASSED", "HAPPY",
                    "NORMAL", "PAINED", "PSYCHOTIC", "SCARED", "SICK", "SNOOTY", "SMITTEN", "TIRED" -> normalized;
            default -> "NORMAL";
        };
    }

    private static float dereActivity(String dereType) {
        return switch (normalizeDere(dereType)) {
            case "DANDERE" -> 0.65F;
            case "KUUDERE" -> 0.8F;
            case "HIMEDERE" -> 1.05F;
            case "NYANDERE" -> 1.15F;
            case "TSUNDERE" -> 1.25F;
            case "DEREDERE" -> 1.4F;
            case "YANDERE" -> 1.5F;
            default -> 1.0F;
        };
    }

    private static boolean isUneasyPair(String observer, String target) {
        return ("A".equals(observer) && "B".equals(target)) || ("B".equals(observer) && "A".equals(target));
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    public record SocialSignal(float affinity, float tension, float interest) {
    }

    public enum SocialMovement {
        APPROACH,
        AVOID,
        IDLE
    }

    public enum SocialPlaceBehavior {
        SHARE,
        ORBIT,
        GUARD,
        AVOID,
        IGNORE
    }

    public enum SocialVisual {
        AFFINITY,
        FAME,
        INTEREST,
        TENSION,
        NONE
    }

    public enum DereReaction {
        CELEBRATE,
        CLING,
        FLUSTER_RETREAT,
        SHY_RETREAT,
        SHOW_OFF,
        OBSERVE,
        NONE
    }
}
