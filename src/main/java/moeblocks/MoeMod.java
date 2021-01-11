package moeblocks;

import moeblocks.init.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Calendar;

@Mod(MoeMod.ID)
public class MoeMod {
    public static final String VERSION = "21.1.10";
    public static final String ID = "moeblocks";
    public static final SimpleChannel CHANNEL = MoeMessages.init(new ResourceLocation(MoeMod.ID));
    public static final ItemGroup ITEMS = new ItemGroup(MoeMod.ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(MoeItems.MOE_DIE.get());
        }
    };
    
    public MoeMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(MoeParticles::registerParticleFactories);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onCommonSetup);
        MoeBlocks.REGISTRY.register(bus);
        MoeEntities.REGISTRY.register(bus);
        MoeItems.REGISTRY.register(bus);
        MoeParticles.REGISTRY.register(bus);
        MoeSounds.REGISTRY.register(bus);
        MoeMessages.register();
    }

    private void onClientSetup(final FMLClientSetupEvent e) {
        MoeBlocks.registerRenderTypes();
        MoeEntities.registerEntityRenderingHandlers();
        MoeItems.registerModelProperties();
        MoeOverrides.registerSpecialRenderers();
    }

    private void onCommonSetup(final FMLCommonSetupEvent e) {
        MoeBlocks.registerPottedPlants();
        MoeConvos.registerConvos();
        MoeEntities.registerAttributes();
        MoeItems.registerDispenserBehaviors();
        MoeOverrides.registerAliases();
        MoeOverrides.registerPropertyOverrides();
        MoeOverrides.registerStepSounds();
        MoeTriggers.registerTriggers();
    }
    
    public static String getVersion() {
        return VERSION;
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }
    
    public static boolean isChristmas() {
        return getCalendar().get(2) + 1 == 12 && getCalendar().get(5) >= 24 && getCalendar().get(5) <= 26;
    }

    public static boolean isHalloween() {
        return getCalendar().get(2) + 1 == 10 && getCalendar().get(5) == 31;
    }
}
