package block_party.registry;

import block_party.BlockParty;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CustomSounds {
    public static final RegistryObject<SoundEvent> AMBIENT_JAPAN = BlockParty.SOUNDS.register("ambient.japan", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("ambient.japan")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_CLOSE = BlockParty.SOUNDS.register("block.shoji_screen.close", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("block.shoji_screen.close")));
    public static final RegistryObject<SoundEvent> BLOCK_SHOJI_SCREEN_OPEN = BlockParty.SOUNDS.register("block.shoji_screen.open", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("block.shoji_screen.open")));
    public static final RegistryObject<SoundEvent> KATANA_PARRY = BlockParty.SOUNDS.register("item.katana.parry", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.katana.parry")));
    public static final RegistryObject<SoundEvent> MOE_BELL_STEP = BlockParty.SOUNDS.register("moe.bell.step", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.bell.step")));
    public static final RegistryObject<SoundEvent> MOE_ANGRY = BlockParty.SOUNDS.register("moe.angry", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.angry")));
    public static final RegistryObject<SoundEvent> MOE_ATTACK = BlockParty.SOUNDS.register("moe.attack", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.attack")));
    public static final RegistryObject<SoundEvent> MOE_CONFUSED = BlockParty.SOUNDS.register("moe.confused", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.confused")));
    public static final RegistryObject<SoundEvent> MOE_CRYING = BlockParty.SOUNDS.register("moe.crying", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.crying")));
    public static final RegistryObject<SoundEvent> MOE_DEAD = BlockParty.SOUNDS.register("moe.dead", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.dead")));
    public static final RegistryObject<SoundEvent> MOE_EAT = BlockParty.SOUNDS.register("moe.eat", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.eat")));
    public static final RegistryObject<SoundEvent> MOE_EQUIP = BlockParty.SOUNDS.register("moe.equip", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.equip")));
    public static final RegistryObject<SoundEvent> MOE_FEED = BlockParty.SOUNDS.register("moe.feed", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.feed")));
    public static final RegistryObject<SoundEvent> MOE_FOLLOW = BlockParty.SOUNDS.register("moe.follow", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.follow")));
    public static final RegistryObject<SoundEvent> MOE_GIGGLE = BlockParty.SOUNDS.register("moe.giggle", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.giggle")));
    public static final RegistryObject<SoundEvent> MOE_GRIEF = BlockParty.SOUNDS.register("moe.grief", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.grief")));
    public static final RegistryObject<SoundEvent> MOE_HAPPY = BlockParty.SOUNDS.register("moe.happy", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.happy")));
    public static final RegistryObject<SoundEvent> MOE_HELLO = BlockParty.SOUNDS.register("moe.hello", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.hello")));
    public static final RegistryObject<SoundEvent> MOE_HURT = BlockParty.SOUNDS.register("moe.hurt", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.hurt")));
    public static final RegistryObject<SoundEvent> MOE_LAUGH = BlockParty.SOUNDS.register("moe.laugh", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.laugh")));
    public static final RegistryObject<SoundEvent> MOE_MEOW = BlockParty.SOUNDS.register("moe.meow", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.meow")));
    public static final RegistryObject<SoundEvent> MOE_NEUTRAL = BlockParty.SOUNDS.register("moe.neutral", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.neutral")));
    public static final RegistryObject<SoundEvent> MOE_NO = BlockParty.SOUNDS.register("moe.no", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.no")));
    public static final RegistryObject<SoundEvent> MOE_PSYCHOTIC = BlockParty.SOUNDS.register("moe.psychotic", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.psychotic")));
    public static final RegistryObject<SoundEvent> MOE_SAY = BlockParty.SOUNDS.register("moe.say", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.say")));
    public static final RegistryObject<SoundEvent> MOE_SENPAI = BlockParty.SOUNDS.register("moe.senpai", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.senpai")));
    public static final RegistryObject<SoundEvent> MOE_SLEEPING = BlockParty.SOUNDS.register("moe.sleeping", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.sleeping")));
    public static final RegistryObject<SoundEvent> MOE_SMITTEN = BlockParty.SOUNDS.register("moe.smitten", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.smitten")));
    public static final RegistryObject<SoundEvent> MOE_SNEEZE = BlockParty.SOUNDS.register("moe.sneeze", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.sneeze")));
    public static final RegistryObject<SoundEvent> MOE_SNICKER = BlockParty.SOUNDS.register("moe.snicker", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.snicker")));
    public static final RegistryObject<SoundEvent> MOE_SNOOTY = BlockParty.SOUNDS.register("moe.snooty", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.snooty")));
    public static final RegistryObject<SoundEvent> MOE_STEP = BlockParty.SOUNDS.register("moe.step", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.step")));
    public static final RegistryObject<SoundEvent> MOE_YAWN = BlockParty.SOUNDS.register("moe.yawn", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.yawn")));
    public static final RegistryObject<SoundEvent> MOE_YES = BlockParty.SOUNDS.register("moe.yes", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("moe.yes")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_BUTTON = BlockParty.SOUNDS.register("item.cell_phone.button", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.cell_phone.button")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_DIAL = BlockParty.SOUNDS.register("item.cell_phone.dial", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.cell_phone.dial")));
    public static final RegistryObject<SoundEvent> ITEM_CELL_PHONE_RING = BlockParty.SOUNDS.register("item.cell_phone.ring", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.cell_phone.ring")));
    public static final RegistryObject<SoundEvent> ITEM_YEARBOOK_REMOVE_PAGE = BlockParty.SOUNDS.register("item.yearbook.remove_page", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("item.yearbook.remove_page")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ANTEATER_SANCTUARY = BlockParty.SOUNDS.register("music_disc.anteater_sanctuary", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("music_disc.anteater_sanctuary")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SAKURA_SAKURA = BlockParty.SOUNDS.register("music_disc.sakura_sakura", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("music_disc.sakura_sakura")));
    public static final RegistryObject<SoundEvent> SILENCE = BlockParty.SOUNDS.register("silence", () -> SoundEvent.createVariableRangeEvent(BlockParty.source("silence")));

    public static void add(DeferredRegister<SoundEvent> registry, IEventBus bus) {
        registry.register(bus);
    }
}
