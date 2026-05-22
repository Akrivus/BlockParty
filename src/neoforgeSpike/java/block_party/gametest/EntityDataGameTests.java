package block_party.gametest;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.goals.HideUntil;
import block_party.registry.CustomEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class EntityDataGameTests {
    private EntityDataGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeEntityTypeCanSpawn(GameTestHelper helper) {
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 0, 1, 0);
        if (moe == null || !moe.isAlive()) {
            helper.fail("Expected block_party:moe to spawn");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeIdentityStateRoundTrips(GameTestHelper helper) {
        UUID owner = new UUID(123L, 456L);
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setDatabaseID(42L);
        moe.setOwnerUUID(owner);
        moe.setBlockState(Blocks.STONE.defaultBlockState());
        moe.setFollowing(true);
        moe.setGivenName("Moe");
        moe.setGender("nonbinary");

        Moe loaded = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        loaded.load(moe.saveWithoutId(new CompoundTag()));

        assertEquals(helper, 42L, loaded.getDatabaseID(), "Moe database ID");
        assertEquals(helper, owner, loaded.getOwnerUUID(), "Moe owner UUID");
        assertEquals(helper, Blocks.STONE.defaultBlockState(), loaded.getBlockState(), "Moe block state");
        assertEquals(helper, true, loaded.isFollowing(), "Moe following flag");
        assertEquals(helper, "Moe", loaded.getGivenName(), "Moe given name");
        assertEquals(helper, "nonbinary", loaded.getGender(), "Moe gender");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeInHidingStateRoundTrips(GameTestHelper helper) {
        BlockPos attachPos = new BlockPos(3, 4, 5);
        MoeInHiding hiding = new MoeInHiding(CustomEntities.MOE_IN_HIDING.get(), helper.getLevel());
        hiding.setDatabaseID(84L);
        hiding.setAttachPos(attachPos);
        hiding.setHideUntil(HideUntil.ONE_SECOND_PASSES);
        hiding.setTicksHidden(17);

        MoeInHiding loaded = new MoeInHiding(CustomEntities.MOE_IN_HIDING.get(), helper.getLevel());
        loaded.load(hiding.saveWithoutId(new CompoundTag()));

        assertEquals(helper, 84L, loaded.getDatabaseID(), "MoeInHiding database ID");
        assertEquals(helper, attachPos, loaded.getAttachPos(), "MoeInHiding attached position");
        assertEquals(helper, HideUntil.ONE_SECOND_PASSES, loaded.getHideUntil(), "MoeInHiding HideUntil");
        assertEquals(helper, 17, loaded.getTicksHidden(), "MoeInHiding ticksHidden");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void registeredEntityIdsRemainStable(GameTestHelper helper) {
        assertEntityId(helper, CustomEntities.MOE.get(), "moe");
        assertEntityId(helper, CustomEntities.MOE_IN_HIDING.get(), "moe_in_hiding");
        helper.succeed();
    }

    private static void assertEntityId(GameTestHelper helper, EntityType<?> type, String path) {
        ResourceLocation expected = BlockParty.source(path);
        ResourceLocation actual = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (!expected.equals(actual)) {
            helper.fail("Expected entity ID " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
