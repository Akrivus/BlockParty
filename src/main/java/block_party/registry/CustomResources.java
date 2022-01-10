package block_party.registry;

import block_party.registry.resources.BlockAliases;
import block_party.registry.resources.DollSounds;
import block_party.registry.resources.DollTextures;
import block_party.registry.resources.SceneManager;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class CustomResources {
    public static final BlockAliases BLOCK_ALIASES = new BlockAliases();
    public static final DollSounds DOLL_SOUNDS = new DollSounds();
    public static final DollTextures DOLL_TEXTURES = new DollTextures();
    public static final SceneManager SCENE_MANAGER = new SceneManager();

    public static void register(IEventBus bus) {
        bus.addListener(CustomResources::registerClientReloadListeners);
        bus.addListener(CustomResources::registerServerReloadListeners);
    }

    private static void registerClientReloadListeners(RegisterClientReloadListenersEvent e) {
        e.registerReloadListener(DOLL_TEXTURES);
    }

    private static void registerServerReloadListeners(AddReloadListenerEvent e) {
        e.addListener(BLOCK_ALIASES);
        e.addListener(DOLL_SOUNDS);
        e.addListener(SCENE_MANAGER);
    }
}
