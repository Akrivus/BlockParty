package block_party.registry.resources;

import block_party.BlockParty;
import block_party.entities.BlockPartyNPC;
import block_party.registry.CustomResources;
import block_party.registry.CustomSounds;
import block_party.utils.JsonUtils;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.function.Supplier;

public class MoeSounds extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = BlockParty.GSON.create();
    private static final Logger LOGGER = LogManager.getLogger();

    public enum Sound {
        ANGRY("angry", CustomSounds.MOE_ANGRY), ATTACK("attack", CustomSounds.MOE_ATTACK), CONFUSED("confused", CustomSounds.MOE_CONFUSED), CRYING("crying", CustomSounds.MOE_CRYING), DEAD("dead", CustomSounds.MOE_DEAD), EAT("eat", CustomSounds.MOE_EAT), EQUIP("equip", CustomSounds.MOE_EQUIP), FEED("feed", CustomSounds.MOE_FEED), FOLLOW("follow", CustomSounds.MOE_FOLLOW), GIGGLE("giggle", CustomSounds.MOE_GIGGLE), GRIEF("grief", CustomSounds.MOE_GRIEF), HAPPY("happy", CustomSounds.MOE_HAPPY), HELLO("hello", CustomSounds.MOE_HELLO), HURT("hurt", CustomSounds.MOE_HURT), LAUGH("laugh", CustomSounds.MOE_LAUGH), MEOW("meow", CustomSounds.MOE_MEOW), NEUTRAL("neutral", CustomSounds.MOE_NEUTRAL), NO("no", CustomSounds.MOE_NO), PSYCHOTIC("psychotic", CustomSounds.MOE_PSYCHOTIC), SAY("say", CustomSounds.MOE_SAY), SENPAI("senpai", CustomSounds.MOE_SENPAI), SLEEPING("sleeping", CustomSounds.MOE_SLEEPING), SMITTEN("smitten", CustomSounds.MOE_SMITTEN), SNEEZE("sneeze", CustomSounds.MOE_SNEEZE), SNICKER("snicker", CustomSounds.MOE_SNICKER), SNOOTY("snooty", CustomSounds.MOE_SNOOTY), STEP("step", CustomSounds.MOE_STEP), YAWN("yawn", CustomSounds.MOE_YAWN), YES("yes", CustomSounds.MOE_YES);

        private final String name;
        private final Supplier<SoundEvent> defaultSoundSupplier;

        Sound(String name, RegistryObject<SoundEvent> defaultSoundSupplier) {
            this.name = name;
            this.defaultSoundSupplier = defaultSoundSupplier;
        }

        public String getName() {
            return this.name;
        }

        public Supplier<SoundEvent> getDefaultSoundSupplier() {
            return this.defaultSoundSupplier;
        }

        public static Map<Sound, Supplier<SoundEvent>> map() {
            ImmutableMap.Builder<Sound, Supplier<SoundEvent>> map = ImmutableMap.builder();
            for (Sound sound : Sound.values()) { map.put(sound, sound.defaultSoundSupplier); }
            return map.build();
        }
    }
    private Map<Block, Map<Sound, Supplier<SoundEvent>>> map = ImmutableMap.of();
    private boolean hasErrors;

    public MoeSounds() {
        super(GSON, "moes/sounds");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> folder, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.hasErrors = false;
        ImmutableMap.Builder<Block, Map<Sound, Supplier<SoundEvent>>> map = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : folder.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "override");
            ImmutableMap.Builder<Sound, Supplier<SoundEvent>> sounds = ImmutableMap.builder();
            Block block = JsonUtils.getAs(JsonUtils.BLOCK, location);

            for (Sound sound : Sound.values()) {
                Supplier<SoundEvent> soundEvent = sound.getDefaultSoundSupplier();
                if (json.has(sound.getName())) {
                    String name = json.get(sound.getName()).getAsString();
                    soundEvent = () -> JsonUtils.getAs(JsonUtils.SOUND_EVENT, name);
                }
                sounds.put(sound, soundEvent);
            }

            map.put(block, sounds.build());
        }
        this.map = map.build();
    }

    public static SoundEvent get(BlockPartyNPC npc, Sound sound) {
        return CustomResources.MOE_SOUNDS.map.getOrDefault(npc.getBlock(), Sound.map()).get(sound).get();
    }
}