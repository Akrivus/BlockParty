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
    public static final String VERSION = "20.29.11";
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
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onCommonSetup);
        this.registerRegisters(bus);
        MoeMessages.register();
    }
    
    private void registerRegisters(IEventBus bus) {
        MoeEntities.REGISTRY.register(bus);
        MoeItems.REGISTRY.register(bus);
        MoeSounds.REGISTRY.register(bus);
    }

    private void onClientSetup(final FMLClientSetupEvent e) {
        MoeEntities.registerEntityRenderingHandlers();
        MoeItems.registerOverrides();
    }

    private void onCommonSetup(final FMLCommonSetupEvent e) {
        MoeBlocks.registerAliases();
        MoeBlocks.registerPropertyOverrides();
        MoeBlocks.registerStepSounds();
        MoeEntities.registerAttributes();
        MoeItems.registerDispenserBehaviors();
    }
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static boolean isChristmas() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
    }
}
