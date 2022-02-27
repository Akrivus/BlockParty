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
    public static final RegistryObject<SoundEvent> KATANA_PARRY = BlockParty.SOUNDS.register("item.katana.parry", () -> new SoundEvent(BlockParty.source("item.katana.parry")));
    public static final RegistryObject<SoundEvent> MOE_BELL_STEP = BlockParty.SOUNDS.register("moe.bell.step", () -> new SoundEvent(BlockParty.source("moe.bell.step")));
    public static final RegistryObject<SoundEvent> MOE_ANGRY = BlockParty.SOUNDS.register("moe.angry", () -> new SoundEvent(BlockParty.source("moe.angry")));
    public static final RegistryObject<SoundEvent> MOE_ATTACK = BlockParty.SOUNDS.register("moe.attack", () -> new SoundEvent(BlockParty.source("moe.attack")));
    public static final RegistryObject<SoundEvent> MOE_CONFUSED = BlockParty.SOUNDS.register("moe.confused", () -> new SoundEvent(BlockParty.source("moe.confused")));
    public static final RegistryObject<SoundEvent> MOE_CRYING = BlockParty.SOUNDS.register("moe.crying", () -> new SoundEvent(BlockParty.source("moe.crying")));
    public static final RegistryObject<SoundEvent> MOE_DEAD = BlockParty.SOUNDS.register("moe.dead", () -> new SoundEvent(BlockParty.source("moe.dead")));
    public static final RegistryObject<SoundEvent> MOE_EAT = BlockParty.SOUNDS.register("moe.eat", () -> new SoundEvent(BlockParty.source("moe.eat")));
    public static final RegistryObject<SoundEvent> MOE_EQUIP = BlockParty.SOUNDS.register("moe.equip", () -> new SoundEvent(BlockParty.source("moe.equip")));
    public static final RegistryObject<SoundEvent> MOE_FEED = BlockParty.SOUNDS.register("moe.feed", () -> new SoundEvent(BlockParty.source("moe.feed")));
    public static final RegistryObject<SoundEvent> MOE_FOLLOW = BlockParty.SOUNDS.register("moe.follow", () -> new SoundEvent(BlockParty.source("moe.follow")));
    public static final RegistryObject<SoundEvent> MOE_GIGGLE = BlockParty.SOUNDS.register("moe.giggle", () -> new SoundEvent(BlockParty.source("moe.giggle")));
    public static final RegistryObject<SoundEvent> MOE_GRIEF = BlockParty.SOUNDS.register("moe.grief", () -> new SoundEvent(BlockParty.source("moe.grief")));
    public static final RegistryObject<SoundEvent> MOE_HAPPY = BlockParty.SOUNDS.register("moe.happy", () -> new SoundEvent(BlockParty.source("moe.happy")));
    public static final RegistryObject<SoundEvent> MOE_HELLO = BlockParty.SOUNDS.register("moe.hello", () -> new SoundEvent(BlockParty.source("moe.hello")));
    public static final RegistryObject<SoundEvent> MOE_HURT = BlockParty.SOUNDS.register("moe.hurt", () -> new SoundEvent(BlockParty.source("moe.hurt")));
    public static final RegistryObject<SoundEvent> MOE_LAUGH = BlockParty.SOUNDS.register("moe.laugh", () -> new SoundEvent(BlockParty.source("moe.laugh")));
    public static final RegistryObject<SoundEvent> MOE_MEOW = BlockParty.SOUNDS.register("moe.meow", () -> new SoundEvent(BlockParty.source("moe.meow")));
    public static final RegistryObject<SoundEvent> MOE_NEUTRAL = BlockParty.SOUNDS.register("moe.neutral", () -> new SoundEvent(BlockParty.source("moe.neutral")));
    public static final RegistryObject<SoundEvent> MOE_NO = BlockParty.SOUNDS.register("moe.no", () -> new SoundEvent(BlockParty.source("moe.no")));
    public static final RegistryObject<SoundEvent> MOE_PSYCHOTIC = BlockParty.SOUNDS.register("moe.psychotic", () -> new SoundEvent(BlockParty.source("moe.psychotic")));
    public static final RegistryObject<SoundEvent> MOE_SAY = BlockParty.SOUNDS.register("moe.say", () -> new SoundEvent(BlockParty.source("moe.say")));
    public static final RegistryObject<SoundEvent> MOE_SENPAI = BlockParty.SOUNDS.register("moe.senpai", () -> new SoundEvent(BlockParty.source("moe.senpai")));
    public static final RegistryObject<SoundEvent> MOE_SLEEPING = BlockParty.SOUNDS.register("moe.sleeping", () -> new SoundEvent(BlockParty.source("moe.sleeping")));
    public static final RegistryObject<SoundEvent> MOE_SMITTEN = BlockParty.SOUNDS.register("moe.smitten", () -> new SoundEvent(BlockParty.source("moe.smitten")));
    public static final RegistryObject<SoundEvent> MOE_SNEEZE = BlockParty.SOUNDS.register("moe.sneeze", () -> new SoundEvent(BlockParty.source("moe.sneeze")));
    public static final RegistryObject<SoundEvent> MOE_SNICKER = BlockParty.SOUNDS.register("moe.snicker", () -> new SoundEvent(BlockParty.source("moe.snicker")));
    public static final RegistryObject<SoundEvent> MOE_SNOOTY = BlockParty.SOUNDS.register("moe.snooty", () -> new SoundEvent(BlockParty.source("moe.snooty")));
    public static final RegistryObject<SoundEvent> MOE_STEP = BlockParty.SOUNDS.register("moe.step", () -> new SoundEvent(BlockParty.source("moe.step")));
    public static final RegistryObject<SoundEvent> MOE_YAWN = BlockParty.SOUNDS.register("moe.yawn", () -> new SoundEvent(BlockParty.source("moe.yawn")));
    public static final RegistryObject<SoundEvent> MOE_YES = BlockParty.SOUNDS.register("moe.yes", () -> new SoundEvent(BlockParty.source("moe.yes")));
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
