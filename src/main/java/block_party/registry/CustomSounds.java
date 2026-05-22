package block_party.registry;

import block_party.BlockParty;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BlockParty.ID);
    public static final Map<String, DeferredHolder<SoundEvent, SoundEvent>> ENTRIES = new LinkedHashMap<>();

    public static final DeferredHolder<SoundEvent, SoundEvent> AMBIENT_JAPAN = register("ambient.japan");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOE_LAUGH = register("moe.laugh");
    public static final DeferredHolder<SoundEvent, SoundEvent> SILENCE = register("silence");

    static {
        registerRemaining(
                "block.shoji_screen.close",
                "block.shoji_screen.open",
                "item.katana.parry",
                "moe.bell.step",
                "moe.angry",
                "moe.attack",
                "moe.confused",
                "moe.crying",
                "moe.dead",
                "moe.eat",
                "moe.equip",
                "moe.feed",
                "moe.follow",
                "moe.giggle",
                "moe.grief",
                "moe.happy",
                "moe.hello",
                "moe.hurt",
                "moe.meow",
                "moe.neutral",
                "moe.no",
                "moe.psychotic",
                "moe.say",
                "moe.senpai",
                "moe.sleeping",
                "moe.smitten",
                "moe.sneeze",
                "moe.snicker",
                "moe.snooty",
                "moe.step",
                "moe.yawn",
                "moe.yes",
                "item.cell_phone.button",
                "item.cell_phone.dial",
                "item.cell_phone.ring",
                "item.yearbook.remove_page",
                "music_disc.anteater_sanctuary",
                "music_disc.sakura_sakura");
    }

    private CustomSounds() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        DeferredHolder<SoundEvent, SoundEvent> sound = SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(BlockParty.source(id)));
        ENTRIES.put(id, sound);
        return sound;
    }

    private static void registerRemaining(String... ids) {
        for (String id : ids) {
            if (!ENTRIES.containsKey(id)) {
                register(id);
            }
        }
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
