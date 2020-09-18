package moe.blocks.mod.init;

import moe.blocks.mod.MoeMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MoeMod.ID);

    public static final RegistryObject<SoundEvent> CELL_PHONE_BUTTON = REGISTRY.register("item.cell_phone.button", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.cell_phone.button")));
    public static final RegistryObject<SoundEvent> CELL_PHONE_DIAL = REGISTRY.register("item.cell_phone.dial", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.cell_phone.dial")));
    public static final RegistryObject<SoundEvent> CELL_PHONE_RING = REGISTRY.register("item.cell_phone.ring", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.cell_phone.ring")));
    public static final RegistryObject<SoundEvent> MOE_ATTACK = REGISTRY.register("entity.moe.attack", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.attack")));
    public static final RegistryObject<SoundEvent> MOE_BELL_STEP = REGISTRY.register("entity.moe.bell.step", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.bell.step")));
    public static final RegistryObject<SoundEvent> MOE_DEAD = REGISTRY.register("entity.moe.dead", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.dead")));
    public static final RegistryObject<SoundEvent> MOE_EAT = REGISTRY.register("entity.moe.eat", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.eat")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_ANGRY = REGISTRY.register("entity.moe.emotion.angry", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.angry")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_BEGGING = REGISTRY.register("entity.moe.emotion.begging", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.begging")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_CONFUSED = REGISTRY.register("entity.moe.emotion.confused", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.confused")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_CRYING = REGISTRY.register("entity.moe.emotion.crying", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.crying")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_EMBARRASSED = REGISTRY.register("entity.moe.emotion.embarrassed", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.embarrassed")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_HAPPY = REGISTRY.register("entity.moe.emotion.happy", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.happy")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_MISCHIEVOUS = REGISTRY.register("entity.moe.emotion.mischievous", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.mischievous")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_NORMAL = REGISTRY.register("entity.moe.emotion.normal", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.normal")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_PAINED = REGISTRY.register("entity.moe.emotion.pained", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.pained")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_PSYCHOTIC = REGISTRY.register("entity.moe.emotion.psychotic", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.psychotic")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_SCARED = REGISTRY.register("entity.moe.emotion.scared", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.scared")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_SMITTEN = REGISTRY.register("entity.moe.emotion.smitten", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.smitten")));
    public static final RegistryObject<SoundEvent> MOE_EMOTION_TIRED = REGISTRY.register("entity.moe.emotion.tired", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.emotion.tired")));
    public static final RegistryObject<SoundEvent> MOE_GREET_LEVEL_1 = REGISTRY.register("entity.moe.greet.level_1", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_1")));
    public static final RegistryObject<SoundEvent> MOE_GREET_LEVEL_2 = REGISTRY.register("entity.moe.greet.level_2", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_2")));
    public static final RegistryObject<SoundEvent> MOE_GREET_LEVEL_3 = REGISTRY.register("entity.moe.greet.level_3", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_3")));
    public static final RegistryObject<SoundEvent> MOE_GREET_LEVEL_4 = REGISTRY.register("entity.moe.greet.level_4", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.greet.level_4")));
    public static final RegistryObject<SoundEvent> MOE_HURT = REGISTRY.register("entity.moe.hurt", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.hurt")));
    public static final RegistryObject<SoundEvent> MOE_NO = REGISTRY.register("entity.moe.no", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.no")));
    public static final RegistryObject<SoundEvent> MOE_SING = REGISTRY.register("entity.moe.sing", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.sing")));
    public static final RegistryObject<SoundEvent> MOE_THANK_YOU = REGISTRY.register("entity.moe.thank_you", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.thank_you")));
    public static final RegistryObject<SoundEvent> MOE_YES = REGISTRY.register("entity.moe.yes", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.yes")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_POMF_POMF = REGISTRY.register("music_disc.pomf_pomf", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "music_disc.pomf_pomf")));
    public static final RegistryObject<SoundEvent> YEARBOOK_REMOVE_PAGE = REGISTRY.register("item.yearbook.remove_page", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.yearbook.remove_page")));
}
