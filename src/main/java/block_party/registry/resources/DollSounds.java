package block_party.registry.resources;

import block_party.BlockParty;
import block_party.npc.BlockPartyNPC;
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

public class DollSounds extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = BlockParty.GSON.create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static DollSounds instance;

    public enum Sound {
        ANGRY("angry", CustomSounds.NPC_ANGRY), ATTACK("attack", CustomSounds.NPC_ATTACK), CONFUSED("confused", CustomSounds.NPC_CONFUSED), CRYING("crying", CustomSounds.NPC_CRYING), DEAD("dead", CustomSounds.NPC_DEAD), EAT("eat", CustomSounds.NPC_EAT), EQUIP("equip", CustomSounds.NPC_EQUIP), FEED("feed", CustomSounds.NPC_FEED), FOLLOW("follow", CustomSounds.NPC_FOLLOW), GIGGLE("giggle", CustomSounds.NPC_GIGGLE), GRIEF("grief", CustomSounds.NPC_GRIEF), HAPPY("happy", CustomSounds.NPC_HAPPY), HELLO("hello", CustomSounds.NPC_HELLO), HURT("hurt", CustomSounds.NPC_HURT), LAUGH("laugh", CustomSounds.NPC_LAUGH), MEOW("meow", CustomSounds.NPC_MEOW), NEUTRAL("neutral", CustomSounds.NPC_NEUTRAL), NO("no", CustomSounds.NPC_NO), PSYCHOTIC("psychotic", CustomSounds.NPC_PSYCHOTIC), SAY("say", CustomSounds.NPC_SAY), SENPAI("senpai", CustomSounds.NPC_SENPAI), SLEEPING("sleeping", CustomSounds.NPC_SLEEPING), SMITTEN("smitten", CustomSounds.NPC_SMITTEN), SNEEZE("sneeze", CustomSounds.NPC_SNEEZE), SNICKER("snicker", CustomSounds.NPC_SNICKER), SNOOTY("snooty", CustomSounds.NPC_SNOOTY), STEP("step", CustomSounds.NPC_STEP), YAWN("yawn", CustomSounds.NPC_YAWN), YES("yes", CustomSounds.NPC_YES);

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

    public DollSounds() {
        super(GSON, "dolls/sounds");
        if (DollSounds.instance != null) { LOGGER.warn("DollSounds was already instantiated; overwriting."); }
        DollSounds.instance = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> sceneFolder, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.hasErrors = false;
        ImmutableMap.Builder<Block, Map<Sound, Supplier<SoundEvent>>> map = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : sceneFolder.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "override");
            ImmutableMap.Builder<Sound, Supplier<SoundEvent>> sounds = ImmutableMap.builder();
            Block block = JsonUtils.getAs(JsonUtils.BLOCK, location);

            for (Sound sound : Sound.values()) {
                Supplier<SoundEvent> soundEvent = sound.getDefaultSoundSupplier();
                if (json.has(sound.getName())) {
                    soundEvent = () -> JsonUtils.getAs(JsonUtils.SOUND_EVENT, json, sound.getName());
                }
                sounds.put(sound, soundEvent);
            }

            map.put(block, sounds.build());
        }
        this.map = map.build();
    }

    public static SoundEvent get(BlockPartyNPC npc, Sound sound) {
        return DollSounds.instance.map.getOrDefault(npc.getBlock(), Sound.map()).get(sound).get();
    }
}