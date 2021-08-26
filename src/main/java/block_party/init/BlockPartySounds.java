package block_party.init;

import block_party.BlockParty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockPartySounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BlockParty.ID);

    public static final RegistryObject<SoundEvent> AMBIENT_JAPAN = REGISTRY.register("ambient.japan", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "ambient.japan")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_CLOSE = REGISTRY.register("block.shoji_screen.close", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "block.shoji_screen.close")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_OPEN = REGISTRY.register("block.shoji_screen.open", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "block.shoji_screen.open")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_BELL_STEP = REGISTRY.register("entity.partyer.bell.step", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.bell.step")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_ANGRY = REGISTRY.register("entity.partyer.angry", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.angry")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_ATTACK = REGISTRY.register("entity.partyer.attack", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.attack")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_CONFUSED = REGISTRY.register("entity.partyer.confused", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.confused")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_CRYING = REGISTRY.register("entity.partyer.crying", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.crying")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_DEAD = REGISTRY.register("entity.partyer.dead", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.dead")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_EAT = REGISTRY.register("entity.partyer.eat", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.eat")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_EQUIP = REGISTRY.register("entity.partyer.equip", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.equip")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_FEED = REGISTRY.register("entity.partyer.feed", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.feed")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_FOLLOW = REGISTRY.register("entity.partyer.follow", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.follow")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_GIGGLE = REGISTRY.register("entity.partyer.giggle", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.giggle")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_GRIEF = REGISTRY.register("entity.partyer.grief", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.grief")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HAPPY = REGISTRY.register("entity.partyer.happy", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.happy")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HELLO = REGISTRY.register("entity.partyer.hello", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.hello")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HURT = REGISTRY.register("entity.partyer.hurt", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.hurt")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_LAUGH = REGISTRY.register("entity.partyer.laugh", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.laugh")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_MEOW = REGISTRY.register("entity.partyer.meow", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.meow")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_NEUTRAL = REGISTRY.register("entity.partyer.neutral", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.neutral")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_NO = REGISTRY.register("entity.partyer.no", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.no")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_PSYCHOTIC = REGISTRY.register("entity.partyer.psychotic", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.psychotic")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SAY = REGISTRY.register("entity.partyer.say", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.say")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SENPAI = REGISTRY.register("entity.partyer.senpai", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.senpai")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SLEEPING = REGISTRY.register("entity.partyer.sleeping", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.sleeping")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SMITTEN = REGISTRY.register("entity.partyer.smitten", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.smitten")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNEEZE = REGISTRY.register("entity.partyer.sneeze", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.sneeze")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNICKER = REGISTRY.register("entity.partyer.snicker", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.snicker")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNOOTY = REGISTRY.register("entity.partyer.snooty", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.snooty")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_YAWN = REGISTRY.register("entity.partyer.yawn", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.yawn")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_YES = REGISTRY.register("entity.partyer.yes", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "entity.partyer.yes")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_BUTTON = REGISTRY.register("item.cell_phone.button", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "item.cell_phone.button")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_DIAL = REGISTRY.register("item.cell_phone.dial", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "item.cell_phone.dial")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_RING = REGISTRY.register("item.cell_phone.ring", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "item.cell_phone.ring")));
    public static final RegistryObject<SoundEvent> ITEM_YEARBOOK_REMOVE_PAGE = REGISTRY.register("item.yearbook.remove_page", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "item.yearbook.remove_page")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ANTEATER_SANCTUARY = REGISTRY.register("music_disc.anteater_sanctuary", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "music_disc.anteater_sanctuary")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SAKURA_SAKURA = REGISTRY.register("music_disc.sakura_sakura", () -> new SoundEvent(new ResourceLocation(BlockParty.ID, "music_disc.sakura_sakura")));
}
