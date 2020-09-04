package moe.blocks.mod;

import moe.blocks.mod.init.*;
import moe.blocks.mod.util.DispenserBehaviors;
import moe.blocks.mod.util.MoeBlockAliases;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(MoeMod.ID)
public class MoeMod {
    public static final String VERSION = "20.21.08";
    public static final String ID = "moeblocks";
    public static final SimpleChannel CHANNEL = MoeMessages.register();

    public MoeMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onCommonSetup);
        this.registerRegisters(bus);
    }

    private void registerRegisters(IEventBus bus) {
        MoeActivities.REGISTRY.register(bus);
        MoeEntities.REGISTRY.register(bus);
        MoeItems.REGISTRY.register(bus);
        MoeSchedules.REGISTRY.register(bus);
        MoeSounds.REGISTRY.register(bus);
    }

    private void onClientSetup(final FMLClientSetupEvent e) {
        MoeEntities.registerEntityRenderingHandlers();
    }

    private void onCommonSetup(final FMLCommonSetupEvent e) {
        DispenserBehaviors.register();
        MoeEntities.registerAttributes();
        MoeBlockAliases.register();
    }
}
