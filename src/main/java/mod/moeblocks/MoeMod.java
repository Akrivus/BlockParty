package mod.moeblocks;

import mod.moeblocks.register.*;
import mod.moeblocks.util.DispenserBehaviors;
import mod.moeblocks.util.MoeBlockAliases;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MoeMod.ID)
public class MoeMod {
    public static final String ID = "moeblocks";

    public MoeMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onCommonSetup);
        this.registerRegisters(bus);
    }

    private void registerRegisters(IEventBus bus) {
        ActivitiesMoe.REGISTRY.register(bus);
        EntityTypesMoe.REGISTRY.register(bus);
        ItemsMoe.REGISTRY.register(bus);
        SchedulesMoe.REGISTRY.register(bus);
        SoundEventsMoe.REGISTRY.register(bus);
    }

    private void onClientSetup(final FMLClientSetupEvent e) {
        EntityTypesMoe.registerEntityRenderingHandlers();
    }

    private void onCommonSetup(final FMLCommonSetupEvent e) {
        DispenserBehaviors.register();
        EntityTypesMoe.registerAttributes();
        MoeBlockAliases.register();
    }
}
