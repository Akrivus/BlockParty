package block_party.client;

import block_party.client.particle.FireflyParticle;
import block_party.client.particle.GinkgoParticle;
import block_party.client.particle.SakuraParticle;
import block_party.client.particle.WhiteSakuraParticle;
import block_party.client.skybox.JapanRenderer;
import block_party.BlockParty;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomItems;
import block_party.registry.CustomParticles;
import block_party.registry.CustomResources;
import block_party.registry.resources.MoeTextureReloadListener;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class BlockPartyClientEvents {
    private BlockPartyClientEvents() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(BlockPartyClientEvents::registerParticleProviders);
        modBus.addListener(BlockPartyClientEvents::registerClientReloadListeners);
        modBus.addListener(BlockPartyClientEvents::registerClientExtensions);
        modBus.addListener(BlockPartyClientEvents::registerRenderTypes);
    }

    public static void registerGameEvents(IEventBus gameBus) {
        gameBus.addListener(JapanRenderer::renderFuji);
        gameBus.addListener(JapanRenderer::addFireflies);
        gameBus.addListener(JapanRenderer::tintFog);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CustomParticles.FIREFLY.get(), FireflyParticle.Factory::new);
        event.registerSpriteSet(CustomParticles.GINKGO.get(), GinkgoParticle.Factory::new);
        event.registerSpriteSet(CustomParticles.SAKURA.get(), SakuraParticle.Factory::new);
        event.registerSpriteSet(CustomParticles.WHITE_SAKURA.get(), WhiteSakuraParticle.Factory::new);
    }

    private static void registerClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(BlockParty.source("moe_textures"), CustomResources.MOE_TEXTURES);
    }

    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        SamuraiArmorClientExtensions.register(event, CustomItems.ENTRIES.get("masked_samurai_kabuto").get());
        SamuraiArmorClientExtensions.register(event, CustomItems.ENTRIES.get("samurai_kabuto").get());
        SamuraiArmorClientExtensions.register(event, CustomItems.ENTRIES.get("samurai_cuirass").get());
        SamuraiArmorClientExtensions.register(event, CustomItems.ENTRIES.get("samurai_chausses").get());
        SamuraiArmorClientExtensions.register(event, CustomItems.ENTRIES.get("samurai_sabaton").get());
    }

    private static void registerRenderTypes(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            setCutout(
                    "blank_hanging_scroll",
                    "black_paper_lantern",
                    "blue_paper_lantern",
                    "brown_paper_lantern",
                    "cyan_paper_lantern",
                    "dawn_hanging_scroll",
                    "evening_hanging_scroll",
                    "gray_paper_lantern",
                    "green_paper_lantern",
                    "light_blue_paper_lantern",
                    "light_gray_paper_lantern",
                    "lime_paper_lantern",
                    "magenta_paper_lantern",
                    "midnight_hanging_scroll",
                    "morning_hanging_scroll",
                    "night_hanging_scroll",
                    "noon_hanging_scroll",
                    "orange_paper_lantern",
                    "pink_paper_lantern",
                    "potted_ginkgo_sapling",
                    "potted_sakura_sapling",
                    "potted_white_sakura_sapling",
                    "potted_wisteria_sapling",
                    "purple_paper_lantern",
                    "red_paper_lantern",
                    "sakura_sapling",
                    "shimenawa",
                    "shoji_panel",
                    "shoji_screen",
                    "shrine_tablet",
                    "white_paper_lantern",
                    "white_sakura_sapling",
                    "wisteria_sapling",
                    "wisteria_vine_body",
                    "wisteria_vine_tip",
                    "yellow_paper_lantern");
            setCutoutMipped(
                    "garden_lantern",
                    "ginkgo_leaves",
                    "ginkgo_sapling",
                    "sakura_blossoms",
                    "white_sakura_blossoms",
                    "wisteria_leaves");
        });
    }

    private static void setCutout(String... ids) {
        setRenderLayer(RenderType.cutout(), ids);
    }

    private static void setCutoutMipped(String... ids) {
        setRenderLayer(RenderType.cutoutMipped(), ids);
    }

    private static void setRenderLayer(RenderType type, String... ids) {
        for (String id : ids) {
            Block block = CustomBlocks.ENTRIES.get(id).get();
            ItemBlockRenderTypes.setRenderLayer(block, type);
        }
    }

    public static boolean hasParticleProviders() {
        return true;
    }

    public static boolean hasClientTextureReloadListener() {
        return CustomResources.MOE_TEXTURES instanceof MoeTextureReloadListener;
    }
}
