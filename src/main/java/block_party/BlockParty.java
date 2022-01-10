package block_party;

import block_party.client.BlockPartyRenderers;
import block_party.registry.*;
import block_party.registry.SceneActions;
import block_party.registry.SceneRequirements;
import com.google.gson.GsonBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Calendar;

@Mod(BlockParty.ID)
public class BlockParty {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, BlockParty.ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BlockParty.ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BlockParty.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BlockParty.ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BlockParty.ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BlockParty.ID);
    public static final DeferredRegister<SceneActions.Factory> SCENE_ACTIONS = DeferredRegister.create(SceneActions.Factory.class, BlockParty.ID);
    public static final DeferredRegister<SceneRequirements.Factory> SCENE_REQUIREMENTS = DeferredRegister.create(SceneRequirements.Factory.class, BlockParty.ID);
    public static final SimpleChannel MESSENGER = CustomMessenger.create();

    public static final String VERSION = "21.12.30";
    public static final String ID = "block_party";
    public static final GsonBuilder GSON = new GsonBuilder();

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

    public BlockParty() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CustomMessenger.register(bus);
        CustomBlockEntities.add(BLOCK_ENTITIES, bus);
        CustomBlocks.add(BLOCKS, bus);
        CustomEntities.add(ENTITIES, bus);
        CustomItems.add(ITEMS, bus);
        CustomParticles.add(PARTICLES, bus);
        CustomSounds.add(SOUNDS, bus);
        SceneActions.add(SCENE_ACTIONS, bus);
        SceneRequirements.add(SCENE_REQUIREMENTS, bus);
        BlockPartyRenderers.register(bus);
        CustomResources.register(MinecraftForge.EVENT_BUS);
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
