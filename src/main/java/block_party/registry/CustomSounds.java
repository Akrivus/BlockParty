package block_party.registry;

import block_party.BlockParty;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CustomSounds {
    public static final RegistryObject<SoundEvent> AMBIENT_JAPAN = BlockParty.SOUNDS.register("ambient.japan", () -> new SoundEvent(BlockParty.source("ambient.japan")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_CLOSE = BlockParty.SOUNDS.register("block.shoji_screen.close", () -> new SoundEvent(BlockParty.source("block.shoji_screen.close")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_OPEN = BlockParty.SOUNDS.register("block.shoji_screen.open", () -> new SoundEvent(BlockParty.source("block.shoji_screen.open")));
    public static final RegistryObject<SoundEvent> NPC_BELL_STEP = BlockParty.SOUNDS.register("npc.bell.step", () -> new SoundEvent(BlockParty.source("npc.bell.step")));
    public static final RegistryObject<SoundEvent> NPC_ANGRY = BlockParty.SOUNDS.register("npc.angry", () -> new SoundEvent(BlockParty.source("npc.angry")));
    public static final RegistryObject<SoundEvent> NPC_ATTACK = BlockParty.SOUNDS.register("npc.attack", () -> new SoundEvent(BlockParty.source("npc.attack")));
    public static final RegistryObject<SoundEvent> NPC_CONFUSED = BlockParty.SOUNDS.register("npc.confused", () -> new SoundEvent(BlockParty.source("npc.confused")));
    public static final RegistryObject<SoundEvent> NPC_CRYING = BlockParty.SOUNDS.register("npc.crying", () -> new SoundEvent(BlockParty.source("npc.crying")));
    public static final RegistryObject<SoundEvent> NPC_DEAD = BlockParty.SOUNDS.register("npc.dead", () -> new SoundEvent(BlockParty.source("npc.dead")));
    public static final RegistryObject<SoundEvent> NPC_EAT = BlockParty.SOUNDS.register("npc.eat", () -> new SoundEvent(BlockParty.source("npc.eat")));
    public static final RegistryObject<SoundEvent> NPC_EQUIP = BlockParty.SOUNDS.register("npc.equip", () -> new SoundEvent(BlockParty.source("npc.equip")));
    public static final RegistryObject<SoundEvent> NPC_FEED = BlockParty.SOUNDS.register("npc.feed", () -> new SoundEvent(BlockParty.source("npc.feed")));
    public static final RegistryObject<SoundEvent> NPC_FOLLOW = BlockParty.SOUNDS.register("npc.follow", () -> new SoundEvent(BlockParty.source("npc.follow")));
    public static final RegistryObject<SoundEvent> NPC_GIGGLE = BlockParty.SOUNDS.register("npc.giggle", () -> new SoundEvent(BlockParty.source("npc.giggle")));
    public static final RegistryObject<SoundEvent> NPC_GRIEF = BlockParty.SOUNDS.register("npc.grief", () -> new SoundEvent(BlockParty.source("npc.grief")));
    public static final RegistryObject<SoundEvent> NPC_HAPPY = BlockParty.SOUNDS.register("npc.happy", () -> new SoundEvent(BlockParty.source("npc.happy")));
    public static final RegistryObject<SoundEvent> NPC_HELLO = BlockParty.SOUNDS.register("npc.hello", () -> new SoundEvent(BlockParty.source("npc.hello")));
    public static final RegistryObject<SoundEvent> NPC_HURT = BlockParty.SOUNDS.register("npc.hurt", () -> new SoundEvent(BlockParty.source("npc.hurt")));
    public static final RegistryObject<SoundEvent> NPC_LAUGH = BlockParty.SOUNDS.register("npc.laugh", () -> new SoundEvent(BlockParty.source("npc.laugh")));
    public static final RegistryObject<SoundEvent> NPC_MEOW = BlockParty.SOUNDS.register("npc.meow", () -> new SoundEvent(BlockParty.source("npc.meow")));
    public static final RegistryObject<SoundEvent> NPC_NEUTRAL = BlockParty.SOUNDS.register("npc.neutral", () -> new SoundEvent(BlockParty.source("npc.neutral")));
    public static final RegistryObject<SoundEvent> NPC_NO = BlockParty.SOUNDS.register("npc.no", () -> new SoundEvent(BlockParty.source("npc.no")));
    public static final RegistryObject<SoundEvent> NPC_PSYCHOTIC = BlockParty.SOUNDS.register("npc.psychotic", () -> new SoundEvent(BlockParty.source("npc.psychotic")));
    public static final RegistryObject<SoundEvent> NPC_SAY = BlockParty.SOUNDS.register("npc.say", () -> new SoundEvent(BlockParty.source("npc.say")));
    public static final RegistryObject<SoundEvent> NPC_SENPAI = BlockParty.SOUNDS.register("npc.senpai", () -> new SoundEvent(BlockParty.source("npc.senpai")));
    public static final RegistryObject<SoundEvent> NPC_SLEEPING = BlockParty.SOUNDS.register("npc.sleeping", () -> new SoundEvent(BlockParty.source("npc.sleeping")));
    public static final RegistryObject<SoundEvent> NPC_SMITTEN = BlockParty.SOUNDS.register("npc.smitten", () -> new SoundEvent(BlockParty.source("npc.smitten")));
    public static final RegistryObject<SoundEvent> NPC_SNEEZE = BlockParty.SOUNDS.register("npc.sneeze", () -> new SoundEvent(BlockParty.source("npc.sneeze")));
    public static final RegistryObject<SoundEvent> NPC_SNICKER = BlockParty.SOUNDS.register("npc.snicker", () -> new SoundEvent(BlockParty.source("npc.snicker")));
    public static final RegistryObject<SoundEvent> NPC_SNOOTY = BlockParty.SOUNDS.register("npc.snooty", () -> new SoundEvent(BlockParty.source("npc.snooty")));
    public static final RegistryObject<SoundEvent> NPC_STEP = BlockParty.SOUNDS.register("npc.step", () -> new SoundEvent(BlockParty.source("npc.step")));
    public static final RegistryObject<SoundEvent> NPC_YAWN = BlockParty.SOUNDS.register("npc.yawn", () -> new SoundEvent(BlockParty.source("npc.yawn")));
    public static final RegistryObject<SoundEvent> NPC_YES = BlockParty.SOUNDS.register("npc.yes", () -> new SoundEvent(BlockParty.source("npc.yes")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_BUTTON = BlockParty.SOUNDS.register("item.cell_phone.button", () -> new SoundEvent(BlockParty.source("item.cell_phone.button")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_DIAL = BlockParty.SOUNDS.register("item.cell_phone.dial", () -> new SoundEvent(BlockParty.source("item.cell_phone.dial")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_RING = BlockParty.SOUNDS.register("item.cell_phone.ring", () -> new SoundEvent(BlockParty.source("item.cell_phone.ring")));
    public static final RegistryObject<SoundEvent> ITEM_YEARBOOK_REMOVE_PAGE = BlockParty.SOUNDS.register("item.yearbook.remove_page", () -> new SoundEvent(BlockParty.source("item.yearbook.remove_page")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ANTEATER_SANCTUARY = BlockParty.SOUNDS.register("music_disc.anteater_sanctuary", () -> new SoundEvent(BlockParty.source("music_disc.anteater_sanctuary")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SAKURA_SAKURA = BlockParty.SOUNDS.register("music_disc.sakura_sakura", () -> new SoundEvent(BlockParty.source("music_disc.sakura_sakura")));
    public static final RegistryObject<SoundEvent> SILENCE = BlockParty.SOUNDS.register("silence", () -> new SoundEvent(BlockParty.source("silence")));

    public static void add(DeferredRegister<SoundEvent> registry, IEventBus bus) {
        registry.register(bus);
    }
}
