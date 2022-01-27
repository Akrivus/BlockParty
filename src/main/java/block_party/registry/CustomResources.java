package block_party.registry;

import block_party.registry.resources.BlockAliases;
import block_party.registry.resources.MoeSounds;
import block_party.registry.resources.MoeTextures;
import block_party.registry.resources.Scenes;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class CustomResources {
    public static final BlockAliases BLOCK_ALIASES = new BlockAliases();
    public static final MoeSounds MOE_SOUNDS = new MoeSounds();
    public static final MoeTextures MOE_TEXTURES = new MoeTextures();
    public static final Scenes SCENES = new Scenes();

    public static void register(IEventBus bus) {
        bus.addListener(CustomResources::registerClientReloadListeners);
        bus.addListener(CustomResources::registerServerReloadListeners);
    }

    private static void registerClientReloadListeners(RegisterClientReloadListenersEvent e) {
        e.registerReloadListener(MOE_TEXTURES);
    }

    private static void registerServerReloadListeners(AddReloadListenerEvent e) {
        e.addListener(BLOCK_ALIASES);
        e.addListener(MOE_SOUNDS);
        e.addListener(SCENES);
    }
}
