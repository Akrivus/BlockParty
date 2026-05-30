package block_party.blocks.entity;

import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.db.BlockPartyDB;
import block_party.network.CustomMessenger;
import block_party.registry.CustomBlockEntities;
import block_party.registry.CustomSounds;
import net.minecraft.nbt.CompoundTag;
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
        return BlockPartyDB.TABLE_SHRINES;
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

        Moe moe = MoeSpawner.spawn(level, spawnPos, Blocks.BELL.defaultBlockState(), this.getPlayerUUID(),
                new CompoundTag(),
                bell -> bell.setDere(MoeSpawner.randomDere(level.random)));
        if (moe == null) {
            return;
        }

        moe.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.TRIGGERED, null);
        moe.playSound(CustomSounds.AMBIENT_JAPAN.get(), 1.0F, 1.0F);
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

}
