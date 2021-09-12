package block_party.init;

import block_party.BlockParty;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockPartySounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BlockParty.ID);

    public static final RegistryObject<SoundEvent> AMBIENT_JAPAN = REGISTRY.register("ambient.japan", () -> new SoundEvent(BlockParty.source("ambient.japan")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_CLOSE = REGISTRY.register("block.shoji_screen.close", () -> new SoundEvent(BlockParty.source("block.shoji_screen.close")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_OPEN = REGISTRY.register("block.shoji_screen.open", () -> new SoundEvent(BlockParty.source("block.shoji_screen.open")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_BELL_STEP = REGISTRY.register("entity.npc.bell.step", () -> new SoundEvent(BlockParty.source("entity.npc.bell.step")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_ANGRY = REGISTRY.register("entity.npc.angry", () -> new SoundEvent(BlockParty.source("entity.npc.angry")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_ATTACK = REGISTRY.register("entity.npc.attack", () -> new SoundEvent(BlockParty.source("entity.npc.attack")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_CONFUSED = REGISTRY.register("entity.npc.confused", () -> new SoundEvent(BlockParty.source("entity.npc.confused")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_CRYING = REGISTRY.register("entity.npc.crying", () -> new SoundEvent(BlockParty.source("entity.npc.crying")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_DEAD = REGISTRY.register("entity.npc.dead", () -> new SoundEvent(BlockParty.source("entity.npc.dead")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_EAT = REGISTRY.register("entity.npc.eat", () -> new SoundEvent(BlockParty.source("entity.npc.eat")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_EQUIP = REGISTRY.register("entity.npc.equip", () -> new SoundEvent(BlockParty.source("entity.npc.equip")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_FEED = REGISTRY.register("entity.npc.feed", () -> new SoundEvent(BlockParty.source("entity.npc.feed")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_FOLLOW = REGISTRY.register("entity.npc.follow", () -> new SoundEvent(BlockParty.source("entity.npc.follow")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_GIGGLE = REGISTRY.register("entity.npc.giggle", () -> new SoundEvent(BlockParty.source("entity.npc.giggle")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_GRIEF = REGISTRY.register("entity.npc.grief", () -> new SoundEvent(BlockParty.source("entity.npc.grief")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HAPPY = REGISTRY.register("entity.npc.happy", () -> new SoundEvent(BlockParty.source("entity.npc.happy")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HELLO = REGISTRY.register("entity.npc.hello", () -> new SoundEvent(BlockParty.source("entity.npc.hello")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_HURT = REGISTRY.register("entity.npc.hurt", () -> new SoundEvent(BlockParty.source("entity.npc.hurt")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_LAUGH = REGISTRY.register("entity.npc.laugh", () -> new SoundEvent(BlockParty.source("entity.npc.laugh")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_MEOW = REGISTRY.register("entity.npc.meow", () -> new SoundEvent(BlockParty.source("entity.npc.meow")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_NEUTRAL = REGISTRY.register("entity.npc.neutral", () -> new SoundEvent(BlockParty.source("entity.npc.neutral")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_NO = REGISTRY.register("entity.npc.no", () -> new SoundEvent(BlockParty.source("entity.npc.no")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_PSYCHOTIC = REGISTRY.register("entity.npc.psychotic", () -> new SoundEvent(BlockParty.source("entity.npc.psychotic")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SAY = REGISTRY.register("entity.npc.say", () -> new SoundEvent(BlockParty.source("entity.npc.say")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SENPAI = REGISTRY.register("entity.npc.senpai", () -> new SoundEvent(BlockParty.source("entity.npc.senpai")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SLEEPING = REGISTRY.register("entity.npc.sleeping", () -> new SoundEvent(BlockParty.source("entity.npc.sleeping")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SMITTEN = REGISTRY.register("entity.npc.smitten", () -> new SoundEvent(BlockParty.source("entity.npc.smitten")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNEEZE = REGISTRY.register("entity.npc.sneeze", () -> new SoundEvent(BlockParty.source("entity.npc.sneeze")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNICKER = REGISTRY.register("entity.npc.snicker", () -> new SoundEvent(BlockParty.source("entity.npc.snicker")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_SNOOTY = REGISTRY.register("entity.npc.snooty", () -> new SoundEvent(BlockParty.source("entity.npc.snooty")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_YAWN = REGISTRY.register("entity.npc.yawn", () -> new SoundEvent(BlockParty.source("entity.npc.yawn")));
    public static final RegistryObject<SoundEvent> ENTITY_MOE_YES = REGISTRY.register("entity.npc.yes", () -> new SoundEvent(BlockParty.source("entity.npc.yes")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_BUTTON = REGISTRY.register("item.cell_phone.button", () -> new SoundEvent(BlockParty.source("item.cell_phone.button")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_DIAL = REGISTRY.register("item.cell_phone.dial", () -> new SoundEvent(BlockParty.source("item.cell_phone.dial")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_RING = REGISTRY.register("item.cell_phone.ring", () -> new SoundEvent(BlockParty.source("item.cell_phone.ring")));
    public static final RegistryObject<SoundEvent> ITEM_YEARBOOK_REMOVE_PAGE = REGISTRY.register("item.yearbook.remove_page", () -> new SoundEvent(BlockParty.source("item.yearbook.remove_page")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ANTEATER_SANCTUARY = REGISTRY.register("music_disc.anteater_sanctuary", () -> new SoundEvent(BlockParty.source("music_disc.anteater_sanctuary")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SAKURA_SAKURA = REGISTRY.register("music_disc.sakura_sakura", () -> new SoundEvent(BlockParty.source("music_disc.sakura_sakura")));
}
