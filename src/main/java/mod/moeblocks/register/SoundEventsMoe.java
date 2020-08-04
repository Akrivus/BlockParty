package mod.moeblocks.register;

import com.sun.org.apache.regexp.internal.RE;
import mod.moeblocks.MoeMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEventsMoe {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MoeMod.ID);

    public static final RegistryObject<SoundEvent> ATTACK = REGISTRY.register("entity.moe.attack", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.attack")));
    public static final RegistryObject<SoundEvent> DEAD = REGISTRY.register("entity.moe.dead", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.dead")));
    public static final RegistryObject<SoundEvent> EAT = REGISTRY.register("entity.moe.eat", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.eat")));
    public static final RegistryObject<SoundEvent> EMOTION_ANGRY = REGISTRY.register("entity.moe.emotion.angry", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.angry")));
    public static final RegistryObject<SoundEvent> EMOTION_BEGGING = REGISTRY.register("entity.moe.emotion.begging", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.begging")));
    public static final RegistryObject<SoundEvent> EMOTION_CONFUSED = REGISTRY.register("entity.moe.emotion.confused", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.confused")));
    public static final RegistryObject<SoundEvent> EMOTION_CRYING = REGISTRY.register("entity.moe.emotion.crying", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.crying")));
    public static final RegistryObject<SoundEvent> EMOTION_EMBARRASSED = REGISTRY.register("entity.moe.emotion.embarrassed", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.embarrassed")));
    public static final RegistryObject<SoundEvent> EMOTION_HAPPY = REGISTRY.register("entity.moe.emotion.happy", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.happy")));
    public static final RegistryObject<SoundEvent> EMOTION_MISCHIEVOUS = REGISTRY.register("entity.moe.emotion.mischievous", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.mischievous")));
    public static final RegistryObject<SoundEvent> EMOTION_NORMAL = REGISTRY.register("entity.moe.emotion.normal", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.normal")));
    public static final RegistryObject<SoundEvent> EMOTION_PAINED = REGISTRY.register("entity.moe.emotion.pained", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.pained")));
    public static final RegistryObject<SoundEvent> EMOTION_PSYCHOTIC = REGISTRY.register("entity.moe.emotion.psychotic", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.psychotic")));
    public static final RegistryObject<SoundEvent> EMOTION_SCARED = REGISTRY.register("entity.moe.emotion.scared", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.scared")));
    public static final RegistryObject<SoundEvent> EMOTION_SMITTEN = REGISTRY.register("entity.moe.emotion.smitten", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.smitten")));
    public static final RegistryObject<SoundEvent> GREET_LEVEL_1 = REGISTRY.register("entity.moe.greet.level_1", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_1")));
    public static final RegistryObject<SoundEvent> GREET_LEVEL_2 = REGISTRY.register("entity.moe.greet.level_2", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_2")));
    public static final RegistryObject<SoundEvent> GREET_LEVEL_3 = REGISTRY.register("entity.moe.greet.level_3", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_3")));
    public static final RegistryObject<SoundEvent> GREET_LEVEL_4 = REGISTRY.register("entity.moe.greet.level_4", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_4")));
    public static final RegistryObject<SoundEvent> HURT = REGISTRY.register("entity.moe.hurt", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.hurt")));
    public static final RegistryObject<SoundEvent> NO = REGISTRY.register("entity.moe.no", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.no")));
    public static final RegistryObject<SoundEvent> SING = REGISTRY.register("entity.moe.sing", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.sing")));
    public static final RegistryObject<SoundEvent> THANK_YOU = REGISTRY.register("entity.moe.thank_you", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.thank_you")));
    public static final RegistryObject<SoundEvent> YES = REGISTRY.register("entity.moe.yes", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.yes")));
}
