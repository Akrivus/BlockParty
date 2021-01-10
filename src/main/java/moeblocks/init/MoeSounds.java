package moeblocks.init;

import moeblocks.MoeMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MoeMod.ID);

    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_CLOSE = REGISTRY.register("block.shoji_screen.close", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "block.shoji_screen.close")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_OPEN = REGISTRY.register("block.shoji_screen.open", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "block.shoji_screen.open")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_BELL_STEP = REGISTRY.register("entity.moe.bell.step", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.bell.step")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_ANGRY = REGISTRY.register("entity.moe.angry", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.angry")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_ATTACK = REGISTRY.register("entity.moe.attack", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.attack")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_CONFUSED = REGISTRY.register("entity.moe.confused", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.confused")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_CRYING = REGISTRY.register("entity.moe.crying", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.crying")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_DEAD = REGISTRY.register("entity.moe.dead", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.dead")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_EAT = REGISTRY.register("entity.moe.eat", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.eat")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_EQUIP = REGISTRY.register("entity.moe.equip", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.equip")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_FEED = REGISTRY.register("entity.moe.feed", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.feed")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_FOLLOW = REGISTRY.register("entity.moe.follow", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.follow")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_GIGGLE = REGISTRY.register("entity.moe.giggle", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.giggle")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_GRIEF = REGISTRY.register("entity.moe.grief", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.grief")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HAPPY = REGISTRY.register("entity.moe.happy", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.happy")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HELLO = REGISTRY.register("entity.moe.hello", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.hello")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HURT = REGISTRY.register("entity.moe.hurt", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.hurt")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_LAUGH = REGISTRY.register("entity.moe.laugh", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.laugh")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_MEOW = REGISTRY.register("entity.moe.meow", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.meow")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_NEUTRAL = REGISTRY.register("entity.moe.neutral", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.neutral")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_NO = REGISTRY.register("entity.moe.no", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.no")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_PSYCHOTIC = REGISTRY.register("entity.moe.psychotic", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.psychotic")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SENPAI = REGISTRY.register("entity.moe.senpai", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.senpai")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SLEEPING = REGISTRY.register("entity.moe.sleeping", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.sleeping")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SMITTEN = REGISTRY.register("entity.moe.smitten", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.smitten")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNEEZE = REGISTRY.register("entity.moe.sneeze", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.sneeze")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNICKER = REGISTRY.register("entity.moe.snicker", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.snicker")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNOOTY = REGISTRY.register("entity.moe.snooty", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.snooty")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_YAWN = REGISTRY.register("entity.moe.yawn", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.yawn")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_YES = REGISTRY.register("entity.moe.yes", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.yes")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_BUTTON = REGISTRY.register("item.cell_phone.button", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.cell_phone.button")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_DIAL = REGISTRY.register("item.cell_phone.dial", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.cell_phone.dial")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_RING = REGISTRY.register("item.cell_phone.ring", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.cell_phone.ring")));
    public static final RegistryObject<SoundEvent> ITEM_YEARBOOK_REMOVE_PAGE = REGISTRY.register("item.yearbook.remove_page", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.yearbook.remove_page")));
}
