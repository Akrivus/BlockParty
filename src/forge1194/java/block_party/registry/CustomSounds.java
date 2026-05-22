package block_party.registry;

import block_party.BlockParty;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CustomSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BlockParty.ID);

    public static final RegistryObject<SoundEvent> AMBIENT_JAPAN = SOUNDS.register("ambient.japan", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("ambient.japan")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_CLOSE = SOUNDS.register("block.shoji_screen.close", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("block.shoji_screen.close")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_OPEN = SOUNDS.register("block.shoji_screen.open", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("block.shoji_screen.open")));
    public static final RegistryObject<SoundEvent> KATANA_PARRY = SOUNDS.register("item.katana.parry", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.katana.parry")));
    public static final RegistryObject<SoundEvent> MOE_BELL_STEP = SOUNDS.register("moe.bell.step", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.bell.step")));
    public static final RegistryObject<SoundEvent> MOE_ANGRY = SOUNDS.register("moe.angry", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.angry")));
    public static final RegistryObject<SoundEvent> MOE_ATTACK = SOUNDS.register("moe.attack", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.attack")));
    public static final RegistryObject<SoundEvent> MOE_CONFUSED = SOUNDS.register("moe.confused", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.confused")));
    public static final RegistryObject<SoundEvent> MOE_CRYING = SOUNDS.register("moe.crying", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.crying")));
    public static final RegistryObject<SoundEvent> MOE_DEAD = SOUNDS.register("moe.dead", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.dead")));
    public static final RegistryObject<SoundEvent> MOE_EAT = SOUNDS.register("moe.eat", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.eat")));
    public static final RegistryObject<SoundEvent> MOE_EQUIP = SOUNDS.register("moe.equip", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.equip")));
    public static final RegistryObject<SoundEvent> MOE_FEED = SOUNDS.register("moe.feed", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.feed")));
    public static final RegistryObject<SoundEvent> MOE_FOLLOW = SOUNDS.register("moe.follow", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.follow")));
    public static final RegistryObject<SoundEvent> MOE_GIGGLE = SOUNDS.register("moe.giggle", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.giggle")));
    public static final RegistryObject<SoundEvent> MOE_GRIEF = SOUNDS.register("moe.grief", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.grief")));
    public static final RegistryObject<SoundEvent> MOE_HAPPY = SOUNDS.register("moe.happy", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.happy")));
    public static final RegistryObject<SoundEvent> MOE_HELLO = SOUNDS.register("moe.hello", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.hello")));
    public static final RegistryObject<SoundEvent> MOE_HURT = SOUNDS.register("moe.hurt", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.hurt")));
    public static final RegistryObject<SoundEvent> MOE_LAUGH = SOUNDS.register("moe.laugh", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.laugh")));
    public static final RegistryObject<SoundEvent> MOE_MEOW = SOUNDS.register("moe.meow", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.meow")));
    public static final RegistryObject<SoundEvent> MOE_NEUTRAL = SOUNDS.register("moe.neutral", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.neutral")));
    public static final RegistryObject<SoundEvent> MOE_NO = SOUNDS.register("moe.no", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.no")));
    public static final RegistryObject<SoundEvent> MOE_PSYCHOTIC = SOUNDS.register("moe.psychotic", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.psychotic")));
    public static final RegistryObject<SoundEvent> MOE_SAY = SOUNDS.register("moe.say", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.say")));
    public static final RegistryObject<SoundEvent> MOE_SENPAI = SOUNDS.register("moe.senpai", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.senpai")));
    public static final RegistryObject<SoundEvent> MOE_SLEEPING = SOUNDS.register("moe.sleeping", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.sleeping")));
    public static final RegistryObject<SoundEvent> MOE_SMITTEN = SOUNDS.register("moe.smitten", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.smitten")));
    public static final RegistryObject<SoundEvent> MOE_SNEEZE = SOUNDS.register("moe.sneeze", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.sneeze")));
    public static final RegistryObject<SoundEvent> MOE_SNICKER = SOUNDS.register("moe.snicker", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.snicker")));
    public static final RegistryObject<SoundEvent> MOE_SNOOTY = SOUNDS.register("moe.snooty", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.snooty")));
    public static final RegistryObject<SoundEvent> MOE_STEP = SOUNDS.register("moe.step", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.step")));
    public static final RegistryObject<SoundEvent> MOE_YAWN = SOUNDS.register("moe.yawn", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.yawn")));
    public static final RegistryObject<SoundEvent> MOE_YES = SOUNDS.register("moe.yes", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.yes")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_BUTTON = SOUNDS.register("item.cell_phone.button", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.cell_phone.button")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_DIAL = SOUNDS.register("item.cell_phone.dial", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.cell_phone.dial")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_RING = SOUNDS.register("item.cell_phone.ring", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.cell_phone.ring")));
    public static final RegistryObject<SoundEvent> ITEM_YEARBOOK_REMOVE_PAGE = SOUNDS.register("item.yearbook.remove_page", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.yearbook.remove_page")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ANTEATER_SANCTUARY = SOUNDS.register("music_disc.anteater_sanctuary", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("music_disc.anteater_sanctuary")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SAKURA_SAKURA = SOUNDS.register("music_disc.sakura_sakura", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("music_disc.sakura_sakura")));
    public static final RegistryObject<SoundEvent> SILENCE = SOUNDS.register("silence", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("silence")));

    public static void add(DeferredRegister<SoundEvent> registry, IEventBus bus) {
        registry.register(bus);
    }
}
