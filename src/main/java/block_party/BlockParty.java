package block_party;

import block_party.client.BlockPartyRenderers;
import block_party.registry.*;
import com.google.gson.GsonBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.*;

@Mod(BlockParty.ID)
public class BlockParty {
    public static final String VERSION = "22.3.6";
    public static final String ID = "block_party";
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BlockParty.ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, BlockParty.ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BlockParty.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, BlockParty.ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, BlockParty.ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BlockParty.ID);
    public static final DeferredRegister<Feature<?>> WORLDGEN_FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, BlockParty.ID);
    public static final DeferredRegister<SceneActions.Builder> SCENE_ACTIONS = DeferredRegister.create(BlockParty.ACTIONS, BlockParty.ID);
    public static final DeferredRegister<SceneFilters.Builder> SCENE_FILTERS = DeferredRegister.create(BlockParty.FILTERS, BlockParty.ID);
    public static final SimpleChannel MESSENGER = CustomMessenger.create();
    public static final GsonBuilder GSON = new GsonBuilder();

    public static final RegistryBuilder<SceneActions.Builder> ACTIONS = new RegistryBuilder<SceneActions.Builder>()
            .setName(BlockParty.source("actions"))
            .setMaxID(Integer.MAX_VALUE - 1);

    public static final RegistryBuilder<SceneFilters.Builder> FILTERS = new RegistryBuilder<SceneFilters.Builder>()
            .setName(BlockParty.source("filters"))
            .setMaxID(Integer.MAX_VALUE - 1);

    /* TODO: Refactor creative tabs.
    public static final CreativeModeTab CreativeModeTab = new CreativeModeTab(BlockParty.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CustomItems.CUPCAKE.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.sort(CustomItems::compare);
        }
    };
     */

    public BlockParty(IEventBus bus) {
        CustomMessenger.register(bus);
        CustomBlockEntities.add(BLOCK_ENTITIES, bus);
        CustomBlocks.add(BLOCKS, bus);
        CustomEntities.add(ENTITIES, bus);
        CustomItems.add(ITEMS, bus);
        CustomParticles.add(PARTICLES, bus);
        CustomSounds.add(SOUNDS, bus);
        CustomWorldGen.Features.add(WORLDGEN_FEATURES, MinecraftForge.EVENT_BUS, bus);
        CustomResources.register(MinecraftForge.EVENT_BUS);
        BlockPartyRenderers.register(bus);
        bus.addListener(this::setup);
    }

    public void setup(FMLCommonSetupEvent e) {
        //e.enqueueWork(() -> CustomWorldGen.Features.setup());
    }

    public static ResourceLocation source(String value) {
        return new ResourceLocation(ID, value);
    }

    public static ResourceLocation source(String value, Object... args) {
        return source(String.format(value, args));
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
