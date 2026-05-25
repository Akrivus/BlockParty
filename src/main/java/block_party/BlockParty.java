package block_party;

import block_party.client.BlockPartyRenderers;
import block_party.client.BlockPartyClientEvents;
import block_party.db.BlockPartyDB;
import block_party.entities.data.HidingSpots;
import block_party.items.SamuraiArmorItem;
import block_party.items.SamuraiKatanaItem;
import block_party.network.CustomMessenger;
import block_party.registry.CustomBlockEntities;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomCreativeTabs;
import block_party.registry.CustomEntities;
import block_party.registry.CustomItems;
import block_party.registry.CustomParticles;
import block_party.registry.CustomResources;
import block_party.registry.CustomSounds;
import block_party.registry.SceneActions;
import block_party.registry.SceneFilters;
import block_party.world.CellPhone;
import block_party.db.voicemail.Voicemails;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Calendar;

@Mod(BlockParty.ID)
public final class BlockParty {
    public static final String ID = "block_party";
    public static final String VERSION = "26.6";

    public BlockParty(IEventBus modBus) {
        CustomBlocks.register(modBus);
        CustomBlockEntities.register(modBus);
        CustomItems.register(modBus);
        CustomCreativeTabs.register(modBus);
        CustomSounds.register(modBus);
        CustomParticles.register(modBus);
        CustomEntities.register(modBus);
        SceneActions.register(modBus);
        SceneFilters.register(modBus);
        modBus.addListener(CustomMessenger::registerPayloads);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            BlockPartyClientEvents.register(modBus);
            BlockPartyClientEvents.registerGameEvents(NeoForge.EVENT_BUS);
            BlockPartyRenderers.register(modBus);
        }
        NeoForge.EVENT_BUS.addListener(CustomResources::registerServerReloadListeners);
        NeoForge.EVENT_BUS.addListener(CustomMessenger::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(Voicemails::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(BlockPartyDB::onServerStarted);
        NeoForge.EVENT_BUS.addListener(BlockPartyDB::onServerStopped);
        NeoForge.EVENT_BUS.addListener(CellPhone::onServerTick);
        NeoForge.EVENT_BUS.addListener(CellPhone::onServerStopped);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onBreakStart);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onBreakEnd);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onPistonPush);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onFalling);
        NeoForge.EVENT_BUS.addListener(SamuraiArmorItem::onXpPickup);
        NeoForge.EVENT_BUS.addListener(SamuraiArmorItem::onArrowImpact);
        NeoForge.EVENT_BUS.addListener(SamuraiKatanaItem::onIncomingDamage);
    }

    public static String getVersion() {
        return VERSION;
    }

    public static boolean isChristmas() {
        Calendar calendar = getCalendar();
        return calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
    }

    public static boolean isHalloween() {
        Calendar calendar = getCalendar();
        return calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DAY_OF_MONTH) == 31;
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public static ResourceLocation source(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
