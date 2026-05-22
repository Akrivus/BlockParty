package block_party;

import block_party.db.BlockPartyDB;
import block_party.entities.data.HidingSpots;
import block_party.network.CustomMessenger;
import block_party.registry.CustomBlockEntities;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomEntities;
import block_party.registry.CustomItems;
import block_party.registry.CustomParticles;
import block_party.registry.CustomResources;
import block_party.registry.CustomSounds;
import block_party.registry.SceneActions;
import block_party.registry.SceneFilters;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BlockParty.ID)
public final class BlockParty {
    public static final String ID = "block_party";
    public static final String VERSION = "22.3.6";

    public BlockParty(IEventBus modBus) {
        CustomBlocks.register(modBus);
        CustomBlockEntities.register(modBus);
        CustomItems.register(modBus);
        CustomSounds.register(modBus);
        CustomParticles.register(modBus);
        CustomEntities.register(modBus);
        SceneActions.register(modBus);
        SceneFilters.register(modBus);
        modBus.addListener(CustomMessenger::registerPayloads);
        NeoForge.EVENT_BUS.addListener(CustomResources::registerServerReloadListeners);
        NeoForge.EVENT_BUS.addListener(BlockPartyDB::onServerStarted);
        NeoForge.EVENT_BUS.addListener(BlockPartyDB::onServerStopped);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onBreakStart);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onBreakEnd);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onPistonPush);
        NeoForge.EVENT_BUS.addListener(HidingSpots::onFalling);
    }

    public static String getVersion() {
        return VERSION;
    }

    public static ResourceLocation source(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
