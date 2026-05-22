package block_party.registry.resources;

import block_party.entities.Moe;
import java.util.Locale;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;

public final class MoeSounds {
    public enum Sound {
        ANGRY("angry"),
        ATTACK("attack"),
        CONFUSED("confused"),
        CRYING("crying"),
        DEAD("dead"),
        EAT("eat"),
        EQUIP("equip"),
        FEED("feed"),
        FOLLOW("follow"),
        GIGGLE("giggle"),
        GRIEF("grief"),
        HAPPY("happy"),
        HELLO("hello"),
        HURT("hurt"),
        LAUGH("laugh"),
        MEOW("meow"),
        NEUTRAL("neutral"),
        NO("no"),
        PSYCHOTIC("psychotic"),
        SAY("say"),
        SENPAI("senpai"),
        SLEEPING("sleeping"),
        SMITTEN("smitten"),
        SNEEZE("sneeze"),
        SNICKER("snicker"),
        SNOOTY("snooty"),
        STEP("step"),
        YAWN("yawn"),
        YES("yes");

        private final String name;

        Sound(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private MoeSounds() {
    }

    public static SoundEvent get(Moe moe, Sound sound) {
        Block block = moe.getVisibleBlockState().getBlock();
        return MoeSoundsReloadListener.get(block, sound.getName());
    }

    public static Sound getEmotionSound(String emotion) {
        if (emotion == null) {
            return Sound.NEUTRAL;
        }
        return switch (emotion.toUpperCase(Locale.ROOT)) {
            case "ANGRY" -> Sound.ANGRY;
            case "CONFUSED" -> Sound.CONFUSED;
            case "CRYING", "SCARED" -> Sound.CRYING;
            case "MISCHIEVOUS" -> Sound.SNICKER;
            case "EMBARRASSED", "PAINED" -> Sound.ATTACK;
            case "HAPPY" -> Sound.GIGGLE;
            case "NORMAL" -> Sound.LAUGH;
            case "PSYCHOTIC" -> Sound.PSYCHOTIC;
            case "SICK" -> Sound.SNEEZE;
            case "SNOOTY" -> Sound.SNOOTY;
            case "SMITTEN" -> Sound.SMITTEN;
            case "TIRED" -> Sound.YAWN;
            default -> Sound.NEUTRAL;
        };
    }
}
