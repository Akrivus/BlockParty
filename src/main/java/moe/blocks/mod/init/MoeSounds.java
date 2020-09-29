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
    public static final RegistryObject<SoundEvent> MOE_AMBIENT = REGISTRY.register("entity.moe.ambient", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.ambient")));
    public static final RegistryObject<SoundEvent> MOE_BELL_STEP = REGISTRY.register("entity.moe.bell.step", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.bell.step")));
    public static final RegistryObject<SoundEvent> MOE_DEAD = REGISTRY.register("entity.moe.dead", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.dead")));
    public static final RegistryObject<SoundEvent> MOE_EAT = REGISTRY.register("entity.moe.eat", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.eat")));
    public static final RegistryObject<SoundEvent> MOE_HURT = REGISTRY.register("entity.moe.hurt", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.hurt")));
    public static final RegistryObject<SoundEvent> MOE_LAUGH = REGISTRY.register("entity.moe.laugh", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.laugh")));
    public static final RegistryObject<SoundEvent> MOE_MOAN = REGISTRY.register("entity.moe.moan", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.moan")));
    public static final RegistryObject<SoundEvent> MOE_NO = REGISTRY.register("entity.moe.no", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.no")));
    public static final RegistryObject<SoundEvent> MOE_SCREAM = REGISTRY.register("entity.moe.scream", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.scream")));
    public static final RegistryObject<SoundEvent> MOE_SING = REGISTRY.register("entity.moe.sing", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.sing")));
    public static final RegistryObject<SoundEvent> MOE_SOB = REGISTRY.register("entity.moe.sob", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.sob")));
    public static final RegistryObject<SoundEvent> MOE_YAWN = REGISTRY.register("entity.moe.yawn", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.yawn")));
    public static final RegistryObject<SoundEvent> MOE_YELL = REGISTRY.register("entity.moe.yell", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.yell")));
    public static final RegistryObject<SoundEvent> MOE_YES = REGISTRY.register("entity.moe.yes", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "entity.moe.yes")));
    public static final RegistryObject<SoundEvent> YEARBOOK_REMOVE_PAGE = REGISTRY.register("item.yearbook.remove_page", () -> new SoundEvent(new ResourceLocation(MoeMod.ID, "item.yearbook.remove_page")));
}
