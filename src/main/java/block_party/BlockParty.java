package block_party;

import block_party.init.*;
import block_party.mob.BlockPartyNPC;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import java.util.Calendar;

@Mod(BlockParty.ID)
public class BlockParty {
    public static final String VERSION = "21.8.25";
    public static final String ID = "block_party";
    public static final SimpleChannel CHANNEL = BlockPartyMessages.init(new ResourceLocation(BlockParty.ID));
    public static final CreativeModeTab ITEMS = new CreativeModeTab(BlockParty.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BlockPartyItems.CUPCAKE.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.sort(BlockPartyItems::compare);
        }
    };

    public BlockParty() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(BlockPartyParticles::registerParticleFactories);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onCommonSetup);
        BlockPartyBlocks.REGISTRY.register(bus);
        BlockPartyEntities.REGISTRY.register(bus);
        BlockPartyItems.REGISTRY.register(bus);
        BlockPartyParticles.REGISTRY.register(bus);
        BlockPartySounds.REGISTRY.register(bus);
        BlockPartyBlockEntities.REGISTRY.register(bus);
    }

    private void onClientSetup(final FMLClientSetupEvent e) {
        BlockPartyBlocks.registerRenderTypes();
        BlockPartyItems.registerModelProperties();
        BlockPartyMessages.registerClient();
        BlockPartyNPC.Overrides.registerSpecialRenderers();
    }

    private void onCommonSetup(final FMLCommonSetupEvent e) {
        BlockPartyBlocks.registerPottedPlants();
        BlockPartyConvos.registerConvos();
        BlockPartyMessages.registerServer();
        BlockPartyNPC.Overrides.registerAliases();
        BlockPartyNPC.Overrides.registerPropertyOverrides();
        BlockPartyNPC.Overrides.registerStepSounds();
    }

    public static ResourceLocation source(String value) {
        return new ResourceLocation(ID, value);
    }

    public static String getVersion() {
        return VERSION;
    }

    public static boolean isChristmas() {
        return getCalendar().get(2) + 1 == 12 && getCalendar().get(5) >= 24 && getCalendar().get(5) <= 26;
    }

    public static boolean isHalloween() {
        return getCalendar().get(2) + 1 == 10 && getCalendar().get(5) == 31;
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }
}
