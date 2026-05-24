package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.network.CustomMessenger;
import block_party.registry.CustomBlockEntities;
import block_party.registry.CustomEntities;
import block_party.registry.CustomSounds;
import java.sql.SQLException;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ShrineTabletBlockEntity extends AbstractDataBlockEntity {
    public ShrineTabletBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.SHRINE_TABLET.get(), pos, state);
    }

    @Override
    public String getTableName() {
        return "Shrines";
    }

    @Override
    public void afterUpdate() {
        if (!(this.level instanceof ServerLevel level)) {
            return;
        }
        BlockPos spawnPos = this.getBlockPos().below(5);
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (lightning != null) {
            lightning.moveTo(Vec3.atBottomCenterOf(spawnPos));
            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }

        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(spawnPos);
        moe.setDatabaseID(spawnPos.asLong());
        moe.setOwnerUUID(this.getPlayerUUID());
        moe.setBlockState(Blocks.BELL.defaultBlockState());
        moe.setDere(randomDere(level.random.nextInt(7)));

        BlockPartyDB db = BlockPartyDB.get(level);
        try {
            NPC row = db.createNpc(level, moe, spawnPos.asLong(), false, BlockPos.ZERO);
            row.applyTo(moe);
            db.addTo(row.playerUuid(), row.databaseId());
        } catch (SQLException exception) {
            return;
        }

        if (level.addFreshEntity(moe)) {
            moe.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.TRIGGERED, null);
            moe.playSound(CustomSounds.AMBIENT_JAPAN.get(), 1.0F, 1.0F);
        }
    }

    @Override
    public void afterChange() {
        if (!(this.level instanceof ServerLevel level)) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            CustomMessenger.sendShrineList(player);
        }
    }

    private static String randomDere(int index) {
        return switch (index) {
            case 1 -> "HIMEDERE";
            case 2 -> "KUUDERE";
            case 3 -> "TSUNDERE";
            case 4 -> "YANDERE";
            case 5 -> "DEREDERE";
            case 6 -> "DANDERE";
            default -> "NYANDERE";
        };
    }
}
