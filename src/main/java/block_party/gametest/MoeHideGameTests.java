package block_party.gametest;

import block_party.BlockParty;
import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import block_party.registry.CustomEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class MoeHideGameTests {
    private MoeHideGameTests() {
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 120)
    public static void leftClickHideSceneHidesAndTimedRevealRestoresMoe(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos spawn = source.east();
        BlockState sourceState = Blocks.DIRT.defaultBlockState();

        helper.setBlock(source, sourceState);
        GameTestSupport.useMoeSpawnEgg(helper, FakePlayerFactory.getMinecraft(helper.getLevel()), source, Direction.EAST);

        Moe moe = GameTestSupport.getSingleMoeNear(helper, spawn);
        BlockPos[] hidePos = new BlockPos[1];
        ServerPlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
        helper.runAfterDelay(2, () -> {
            hidePos[0] = moe.blockPosition();
            moe.hurt(helper.getLevel().damageSources().playerAttack(player), 1.0F);
        });

        helper.runAfterDelay(5, () -> {
            GameTestSupport.assertBlockAt(helper, Blocks.DIRT, hidePos[0]);
            GameTestSupport.assertNoMoesNearAbsolute(helper, hidePos[0]);
            MoeInHiding hidden = GameTestSupport.getSingleHiddenMoeNearAbsolute(helper, hidePos[0]);
            if (!hidePos[0].equals(hidden.getAttachPos())) {
                helper.fail("Expected hidden Moe to attach to " + hidePos[0] + ", got " + hidden.getAttachPos());
            }
        });

        helper.runAfterDelay(35, () -> {
            GameTestSupport.assertBlockAt(helper, Blocks.AIR, hidePos[0]);
            Moe revealed = GameTestSupport.getSingleMoeNearAbsolute(helper, hidePos[0]);
            if (!sourceState.equals(revealed.getActualBlockState())) {
                helper.fail("Expected timed reveal to preserve " + sourceState + ", got " + revealed.getActualBlockState());
            }
            GameTestSupport.assertNoHiddenMoesNearAbsolute(helper, hidePos[0]);
            helper.succeed();
        });
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 120)
    public static void hiddenSpotDisturbanceRevealsMoe(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos spawn = source.east();
        BlockState sourceState = Blocks.DIRT.defaultBlockState();

        helper.setBlock(source, sourceState);
        GameTestSupport.useMoeSpawnEgg(helper, FakePlayerFactory.getMinecraft(helper.getLevel()), source, Direction.EAST);

        Moe moe = GameTestSupport.getSingleMoeNear(helper, spawn);
        BlockPos hidePos = moe.blockPosition();
        moe.hide(HideUntil.EXPOSED);

        helper.runAfterDelay(2, () -> {
            GameTestSupport.assertBlockAt(helper, Blocks.DIRT, hidePos);
            GameTestSupport.assertNoMoesNearAbsolute(helper, hidePos);
            GameTestSupport.getSingleHiddenMoeNearAbsolute(helper, hidePos);

            boolean revealed = HidingSpots.spawn(
                    helper.getLevel(),
                    new DimBlockPos(helper.getLevel().dimension(), hidePos)
            );
            if (!revealed) {
                helper.fail("Expected HidingSpots disturbance spawn to reveal the hidden Moe");
            }

            GameTestSupport.assertBlockAt(helper, Blocks.AIR, hidePos);
            Moe disturbed = GameTestSupport.getSingleMoeNearAbsolute(helper, hidePos);
            if (!sourceState.equals(disturbed.getActualBlockState())) {
                helper.fail("Expected disturbed reveal to preserve " + sourceState + ", got " + disturbed.getActualBlockState());
            }
            GameTestSupport.assertNoHiddenMoesNearAbsolute(helper, hidePos);
            helper.succeed();
        });
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 120)
    public static void hiddenMoeSerializationReloadRevealRestoresIdentity(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos spawn = source.east();
        BlockState sourceState = Blocks.DIRT.defaultBlockState();

        helper.setBlock(source, sourceState);
        GameTestSupport.useMoeSpawnEgg(helper, FakePlayerFactory.getMinecraft(helper.getLevel()), source, Direction.EAST);

        Moe moe = GameTestSupport.getSingleMoeNear(helper, spawn);
        long databaseID = moe.getDatabaseID();
        String givenName = moe.getGivenName();
        BlockPos hidePos = moe.blockPosition();
        moe.hide(HideUntil.ONE_SECOND_PASSES);

        helper.runAfterDelay(3, () -> {
            MoeInHiding hidden = GameTestSupport.getSingleHiddenMoeNearAbsolute(helper, hidePos);
            if (hidden.getTicksHidden() <= 0) {
                helper.fail("Expected hidden Moe to accumulate ticks before serialization");
            }

            CompoundTag saved = new CompoundTag();
            hidden.addAdditionalSaveData(saved);
            hidden.remove(Entity.RemovalReason.DISCARDED);

            MoeInHiding restored = new MoeInHiding(CustomEntities.MOE_IN_HIDING.get(), helper.getLevel());
            restored.readAdditionalSaveData(saved);
            restored.absMoveTo(hidePos.getX() + 0.5D, hidePos.getY(), hidePos.getZ() + 0.5D);
            if (!helper.getLevel().addFreshEntity(restored)) {
                helper.fail("Expected reloaded hidden Moe marker to join the level");
            }

            if (restored.getHideUntil() != HideUntil.ONE_SECOND_PASSES) {
                helper.fail("Expected HideUntil to survive reload, got " + restored.getHideUntil());
            }
            if (restored.getTicksHidden() != saved.getInt("TicksHidden")) {
                helper.fail("Expected ticksHidden to survive reload");
            }
            if (!hidePos.equals(restored.getAttachPos())) {
                helper.fail("Expected hidden position to survive reload, got " + restored.getAttachPos());
            }
            if (restored.getDatabaseID() != databaseID) {
                helper.fail("Expected hidden position to stay associated with NPC " + databaseID + ", got " + restored.getDatabaseID());
            }

            boolean revealed = HidingSpots.spawn(
                    helper.getLevel(),
                    new DimBlockPos(helper.getLevel().dimension(), hidePos)
            );
            if (!revealed) {
                helper.fail("Expected reloaded hidden Moe to reveal from persisted hiding spot");
            }

            Moe revealedMoe = GameTestSupport.getSingleMoeNearAbsolute(helper, hidePos);
            if (revealedMoe.getDatabaseID() != databaseID) {
                helper.fail("Expected reveal to restore NPC " + databaseID + ", got " + revealedMoe.getDatabaseID());
            }
            if (!givenName.equals(revealedMoe.getGivenName())) {
                helper.fail("Expected reveal to restore Moe name " + givenName + ", got " + revealedMoe.getGivenName());
            }
            if (!sourceState.equals(revealedMoe.getActualBlockState())) {
                helper.fail("Expected reload reveal to preserve " + sourceState + ", got " + revealedMoe.getActualBlockState());
            }
            GameTestSupport.assertNoHiddenMoesNearAbsolute(helper, hidePos);
            helper.succeed();
        });
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void missingHiddenSpotSpawnAndRevealEventsNoOp(GameTestHelper helper) {
        BlockPos normalBlock = new BlockPos(1, 1, 1);
        BlockPos piston = new BlockPos(3, 1, 1);
        BlockPos falling = new BlockPos(5, 1, 1);
        ServerPlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());

        helper.setBlock(normalBlock, Blocks.DIRT.defaultBlockState());
        helper.setBlock(piston, Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.EAST));
        helper.setBlock(falling, Blocks.SAND.defaultBlockState());

        boolean revealed = HidingSpots.spawn(
                helper.getLevel(),
                new DimBlockPos(helper.getLevel().dimension(), helper.absolutePos(normalBlock))
        );
        if (revealed) {
            helper.fail("Expected missing hidden spot lookup to no-op");
        }

        BlockPos absoluteNormalBlock = helper.absolutePos(normalBlock);
        HidingSpots.onBreakStart(new PlayerInteractEvent.LeftClickBlock(player, absoluteNormalBlock, Direction.UP));
        HidingSpots.onBreakEnd(new BlockEvent.BreakEvent(
                helper.getLevel(),
                absoluteNormalBlock,
                Blocks.DIRT.defaultBlockState(),
                player
        ));
        HidingSpots.onPistonPush(new PistonEvent.Pre(
                helper.getLevel(),
                helper.absolutePos(piston),
                Direction.EAST,
                PistonEvent.PistonMoveType.EXTEND
        ));

        FallingBlockEntity fallingEntity = FallingBlockEntity.fall(
                helper.getLevel(),
                helper.absolutePos(falling),
                Blocks.SAND.defaultBlockState()
        );
        EntityJoinLevelEvent fallingEvent = new EntityJoinLevelEvent(fallingEntity, helper.getLevel());
        HidingSpots.onFalling(fallingEvent);
        if (fallingEvent.isCanceled()) {
            helper.fail("Expected falling block without hidden spot to continue joining the level");
        }

        GameTestSupport.assertBlockAt(helper, Blocks.DIRT, helper.absolutePos(normalBlock));
        GameTestSupport.assertNoMoesNearAbsolute(helper, absoluteNormalBlock);
        GameTestSupport.assertNoHiddenMoesNearAbsolute(helper, absoluteNormalBlock);
        helper.succeed();
    }
}
