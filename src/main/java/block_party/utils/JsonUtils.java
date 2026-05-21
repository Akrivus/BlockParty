package block_party.utils;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation BIOME = new ResourceLocation("minecraft:worldgen/biome");
    public static final ResourceLocation BLOCK = new ResourceLocation("minecraft:block");
    public static final ResourceLocation ENTITY_TYPE = new ResourceLocation("minecraft:entity_type");
    public static final ResourceLocation ITEM = new ResourceLocation("minecraft:item");
    public static final ResourceLocation MOB_EFFECT = new ResourceLocation("minecraft:mob_effect");
    public static final ResourceLocation PARTICLE_TYPE = new ResourceLocation("minecraft:particle_type");
    public static final ResourceLocation SOUND_EVENT = new ResourceLocation("minecraft:sound_event");
    public static final ResourceLocation SCENE_ACTION = new ResourceLocation("block_party:scene_action");
    public static final ResourceLocation SCENE_FILTER = new ResourceLocation("block_party:scene_filter");
    public static final ResourceLocation SCENE_ALWAYS = new ResourceLocation("block_party:always");
    public static final ResourceLocation SCENE_HIDE = new ResourceLocation("block_party:hide");

    public static ResourceLocation getAsResourceLocation(JsonObject json, String key) {
        return new ResourceLocation(GsonHelper.getAsString(json, key));
    }

    public static ResourceLocation getAsResourceLocation(JsonObject json, String key, String def) {
        return new ResourceLocation(GsonHelper.getAsString(json, key, def));
    }

    public static <T> T getAs(ResourceLocation registry, ResourceLocation location) {
        if (BLOCK.equals(registry)) {
            return getFrom(BuiltInRegistries.BLOCK, registry, location);
        }
        if (ENTITY_TYPE.equals(registry)) {
            return getFrom(BuiltInRegistries.ENTITY_TYPE, registry, location);
        }
        if (ITEM.equals(registry)) {
            return getFrom(BuiltInRegistries.ITEM, registry, location);
        }
        if (SOUND_EVENT.equals(registry)) {
            return getFrom(BuiltInRegistries.SOUND_EVENT, registry, location);
        }
        LOGGER.error("Unsupported registry lookup: registry={}, location={}", registry, location);
        return null;
    }

    public static <T> T getAs(ResourceLocation registry, String location)
    {
        ResourceLocation parsed = ResourceLocation.tryParse(location);
        if (parsed == null) {
            LOGGER.error("Invalid resource location for registry lookup: registry={}, location={}", registry, location);
            return null;
        }
        return getAs(registry, parsed);
    }

    private static <T, V> T getFrom(Registry<V> registry, ResourceLocation registryName, ResourceLocation location) {
        V value = registry.getOptional(location).orElse(null);
        if (value == null) {
            LOGGER.error("Missing registry entry: registry={}, location={}", registryName, location);
            return null;
        }
        return (T) value;
    }
}
