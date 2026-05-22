package block_party.gametest;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.items.LetterItem;
import block_party.items.MoeMusicItem;
import block_party.items.MoeBlockItem;
import block_party.items.SamuraiArmorItem;
import block_party.items.SamuraiKatanaItem;
import block_party.items.SortableItem;
import block_party.registry.CustomCreativeTabs;
import block_party.registry.CustomEntities;
import block_party.registry.CustomItems;
import block_party.registry.CustomTags;
import block_party.registry.SceneActions;
import block_party.registry.SceneFilters;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class RegistryGameTests {
    private RegistryGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void representativeRegistryIdsExist(GameTestHelper helper) {
        assertRegistered(helper, BuiltInRegistries.BLOCK, "shoji_block");
        assertRegistered(helper, BuiltInRegistries.ITEM, "moe_spawn_egg");
        assertRegistered(helper, BuiltInRegistries.SOUND_EVENT, "moe.laugh");
        assertRegistered(helper, BuiltInRegistries.PARTICLE_TYPE, "sakura");
        assertRegistered(helper, BuiltInRegistries.ENTITY_TYPE, "moe");
        assertRegistered(helper, BuiltInRegistries.CREATIVE_MODE_TAB, "block_party");
        assertRegistered(helper, SceneActions.REGISTRY, "send_dialogue");
        assertRegistered(helper, SceneFilters.REGISTRY, "always");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void creativeTabContainsManualReviewItems(GameTestHelper helper) {
        List<ItemStack> stacks = CustomCreativeTabs.reviewStacks();
        assertTabContains(helper, stacks, "moe_spawn_egg");
        assertTabContains(helper, stacks, "cell_phone");
        assertTabContains(helper, stacks, "yearbook");
        assertTabContains(helper, stacks, "shrine_tablet");
        assertTabContains(helper, stacks, "garden_lantern");
        assertTabContains(helper, stacks, "sakura_sapling");
        assertTabContains(helper, stacks, "sakura_log");
        assertTabContains(helper, stacks, "sakura_blossoms");
        assertTabContains(helper, stacks, "wisteria_vines");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeBlockItemsExposeForgeSortOrders(GameTestHelper helper) {
        assertMoeBlockItem(helper, "shrine_tablet", 5);
        assertMoeBlockItem(helper, "shimenawa", 6);
        assertMoeBlockItem(helper, "sakura_sapling", 10);
        assertMoeBlockItem(helper, "sakura_log", 20);
        assertMoeBlockItem(helper, "blank_hanging_scroll", 90);
        assertMoeBlockItem(helper, "black_paper_lantern", 100);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void creativeTabUsesForgeSortMetadata(GameTestHelper helper) {
        List<ItemStack> stacks = CustomCreativeTabs.reviewStacks();
        assertBefore(helper, stacks, "moe_spawn_egg", "yearbook");
        assertBefore(helper, stacks, "yearbook", "letter");
        assertBefore(helper, stacks, "letter", "calligraphy_brush");
        assertBefore(helper, stacks, "calligraphy_brush", "shrine_tablet");
        assertBefore(helper, stacks, "shimenawa", "sakura_sapling");
        assertBefore(helper, stacks, "sakura_log", "blank_hanging_scroll");
        assertBefore(helper, stacks, "blank_hanging_scroll", "black_paper_lantern");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void simpleCustomItemsExposeForgeReviewProperties(GameTestHelper helper) {
        assertFood(helper, "bento_box", 11);
        assertFood(helper, "cupcake", 2);
        assertFood(helper, "onigiri", 2);
        assertStackAndDamage(helper, "calligraphy_brush", 1, true);
        assertStackAndDamage(helper, "yearbook_page", 1, false);
        assertStackAndDamage(helper, "music_disc_sakura_sakura", 1, false);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void letterKeepsForgeOpenClosedState(GameTestHelper helper) {
        ItemStack open = new ItemStack(CustomItems.ENTRIES.get("letter").get());
        if (!(open.getItem() instanceof LetterItem) || !(open.getItem() instanceof SortableItem sortable) || sortable.getSortOrder() != 3) {
            helper.fail("Expected letter to use the Forge LetterItem sort metadata");
            return;
        }
        if (LetterItem.isClosed(open) != 0.0F || LetterItem.isOpen(open) != 1.0F) {
            helper.fail("Expected a letter with no IsClosed tag to be open");
            return;
        }
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("IsClosed", true);
        open.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        if (LetterItem.isClosed(open) != 1.0F || LetterItem.isOpen(open) != 0.0F) {
            helper.fail("Expected IsClosed custom data to close the letter");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void musicDiscsUseJukeboxComponents(GameTestHelper helper) {
        assertMusicDisc(helper, "music_disc_anteater_sanctuary");
        assertMusicDisc(helper, "music_disc_sakura_sakura");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void samuraiWeaponsUseForgeToolProfilesAndTags(GameTestHelper helper) {
        ItemStack katana = new ItemStack(CustomItems.ENTRIES.get("samurai_katana").get());
        ItemStack bokken = new ItemStack(CustomItems.ENTRIES.get("wooden_bokken").get());
        if (!(katana.getItem() instanceof SamuraiKatanaItem) || katana.getMaxDamage() != 288 || katana.getRarity() != Rarity.RARE) {
            helper.fail("Expected samurai_katana to use the Forge katana profile");
            return;
        }
        if (!(bokken.getItem() instanceof SamuraiKatanaItem) || bokken.getMaxDamage() != 88 || bokken.getRarity() != Rarity.UNCOMMON) {
            helper.fail("Expected wooden_bokken to use the Forge bokken profile");
            return;
        }
        if (!katana.is(CustomTags.Items.PARRY_SWORDS) || !bokken.is(CustomTags.Items.PARRY_SWORDS)) {
            helper.fail("Expected katana and bokken to remain parry swords");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void samuraiArmorUsesTagsAndServerHelpers(GameTestHelper helper) {
        ItemStack kabuto = new ItemStack(CustomItems.ENTRIES.get("samurai_kabuto").get());
        ItemStack mask = new ItemStack(CustomItems.ENTRIES.get("masked_samurai_kabuto").get());
        if (!(kabuto.getItem() instanceof SamuraiArmorItem) || kabuto.getRarity() != Rarity.RARE) {
            helper.fail("Expected samurai_kabuto to use the Forge samurai armor item");
            return;
        }
        if (!(mask.getItem() instanceof SamuraiArmorItem) || mask.getRarity() != Rarity.EPIC) {
            helper.fail("Expected masked_samurai_kabuto to keep epic rarity");
            return;
        }
        kabuto.setDamageValue(10);
        int leftover = SamuraiArmorItem.repairWithExperience(kabuto, 4);
        if (leftover != 0 || kabuto.getDamageValue() != 6) {
            helper.fail("Expected samurai XP repair helper to consume XP into durability");
            return;
        }
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 0, 1, 0);
        moe.setItemSlot(EquipmentSlot.HEAD, kabuto);
        moe.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(CustomItems.ENTRIES.get("samurai_katana").get()));
        double reduced = SamuraiArmorItem.reduceArrowDamageForSamuraiItems(moe, 16.0D);
        if (reduced != 4.0D) {
            helper.fail("Expected each equipped samurai item to halve arrow damage, got " + reduced);
            return;
        }
        helper.succeed();
    }

    private static void assertRegistered(GameTestHelper helper, Registry<?> registry, String path) {
        ResourceLocation id = BlockParty.source(path);
        if (!registry.containsKey(id)) {
            helper.fail("Expected registry ID " + id + " in " + registry.key().location());
        }
    }

    private static void assertTabContains(GameTestHelper helper, List<ItemStack> stacks, String path) {
        Item item = BuiltInRegistries.ITEM.getValue(BlockParty.source(path));
        if (item == null || stacks.stream().noneMatch(stack -> stack.is(item))) {
            helper.fail("Expected Block Party creative tab to contain " + BlockParty.source(path));
        }
    }

    private static void assertMoeBlockItem(GameTestHelper helper, String path, int sortOrder) {
        Item item = CustomItems.ENTRIES.get(path).get();
        if (!(item instanceof MoeBlockItem) || !(item instanceof SortableItem sortable)) {
            helper.fail("Expected " + path + " to be a sortable MoeBlockItem");
            return;
        }
        if (sortable.getSortOrder() != sortOrder) {
            helper.fail("Expected " + path + " sort order to be " + sortOrder + ", got " + sortable.getSortOrder());
        }
    }

    private static void assertBefore(GameTestHelper helper, List<ItemStack> stacks, String first, String second) {
        int firstIndex = indexOf(stacks, first);
        int secondIndex = indexOf(stacks, second);
        if (firstIndex < 0 || secondIndex < 0 || firstIndex >= secondIndex) {
            helper.fail("Expected " + first + " before " + second + " in creative tab, got " + firstIndex + " and " + secondIndex);
        }
    }

    private static int indexOf(List<ItemStack> stacks, String path) {
        Item item = BuiltInRegistries.ITEM.getValue(BlockParty.source(path));
        for (int index = 0; index < stacks.size(); ++index) {
            if (stacks.get(index).is(item)) {
                return index;
            }
        }
        return -1;
    }

    private static void assertFood(GameTestHelper helper, String path, int nutrition) {
        ItemStack stack = new ItemStack(CustomItems.ENTRIES.get(path).get());
        var food = stack.get(DataComponents.FOOD);
        if (food == null || food.nutrition() != nutrition) {
            helper.fail("Expected " + path + " food nutrition to be " + nutrition + ", got " + food);
        }
    }

    private static void assertMusicDisc(GameTestHelper helper, String path) {
        ItemStack stack = new ItemStack(CustomItems.ENTRIES.get(path).get());
        if (!(stack.getItem() instanceof MoeMusicItem) || !(stack.getItem() instanceof SortableItem sortable) || sortable.getSortOrder() != 100) {
            helper.fail("Expected " + path + " to use the Forge music disc item");
            return;
        }
        if (stack.getMaxStackSize() != 1 || stack.get(DataComponents.JUKEBOX_PLAYABLE) == null) {
            helper.fail("Expected " + path + " to be a one-stack jukebox playable disc");
        }
    }

    private static void assertStackAndDamage(GameTestHelper helper, String path, int maxStackSize, boolean damageable) {
        ItemStack stack = new ItemStack(CustomItems.ENTRIES.get(path).get());
        if (stack.getMaxStackSize() != maxStackSize) {
            helper.fail("Expected " + path + " max stack size to be " + maxStackSize + ", got " + stack.getMaxStackSize());
            return;
        }
        if (stack.isDamageableItem() != damageable) {
            helper.fail("Expected " + path + " damageable to be " + damageable);
        }
    }
}
