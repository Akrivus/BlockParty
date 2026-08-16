package block_party.conversation.model;

import java.util.List;
import java.util.Locale;

/** Presentation keys understood by the in-game Moe renderer. */
public final class SpeakerPresentation {
    public static final List<String> EMOTIONS = List.of(
            "ANGRY", "BEGGING", "CONFUSED", "CRYING", "MISCHIEVOUS",
            "EMBARRASSED", "HAPPY", "NORMAL", "PAINED", "PSYCHOTIC",
            "SCARED", "SICK", "SNOOTY", "SMITTEN", "TIRED");
    public static final List<String> ANIMATIONS = List.of(
            "DEFAULT", "AWE", "BEG", "HAPPY_DANCE", "LOOK_AROUND",
            "SHIVER", "YEARBOOK", "WAVE");

    private SpeakerPresentation() {
    }

    public static boolean validEmotion(String value) {
        return value != null && EMOTIONS.contains(normalize(value));
    }

    public static boolean validAnimation(String value) {
        return value != null && ANIMATIONS.contains(normalize(value));
    }

    public static String normalize(String value) {
        return value.toUpperCase(Locale.ROOT);
    }
}
