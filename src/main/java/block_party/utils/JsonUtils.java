package block_party.utils;

import block_party.BlockParty;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class JsonUtils {
    public static final ResourceLocation BIOME = new ResourceLocation("minecraft:worldgen/biome");
    public static final ResourceLocation BLOCK = new ResourceLocation("minecraft:block");
    public static final ResourceLocation ENTITY_TYPE = new ResourceLocation("minecraft:entity_type");
    public static final ResourceLocation ITEM = new ResourceLocation("minecraft:item");
    public static final ResourceLocation MOB_EFFECT = new ResourceLocation("minecraft:mob_effect");
    public static final ResourceLocation PARTICLE_TYPE = new ResourceLocation("minecraft:particle_type");
    public static final ResourceLocation SOUND_EVENT = new ResourceLocation("minecraft:sound_event");
    public static final ResourceLocation SCENE_ACTION = new ResourceLocation("block_party:scene_action");
    public static final ResourceLocation SCENE_REQUIREMENT = new ResourceLocation("block_party:scene_requirement");

    public static <T extends IForgeRegistryEntry> T getAs(ResourceLocation registry, ResourceLocation location) {
        return (T) RegistryManager.ACTIVE.getRegistry(registry).getValue(location);
    }

    public static <T extends IForgeRegistryEntry> T getAs(ResourceLocation registry, String location) {
        return getAs(registry, new ResourceLocation(location));
    }

    public static <T extends IForgeRegistryEntry> T getAs(ResourceLocation registry, JsonObject json, String key) {
        return getAs(registry, getAsResourceLocation(json, key));
    }

    public static ResourceLocation getAsResourceLocation(JsonObject json, String key) {
        return new ResourceLocation(GsonHelper.getAsString(json, key));
    }

    public static ResourceLocation getAsResourceLocation(JsonObject json, String key, String def) {
        return new ResourceLocation(GsonHelper.getAsString(json, key, def));
    }
}
