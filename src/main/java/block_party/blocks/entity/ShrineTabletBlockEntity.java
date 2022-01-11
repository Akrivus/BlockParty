package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.db.records.Shrine;
import block_party.messages.SShrineList;
import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.trait.Dere;
import block_party.registry.*;
import block_party.scene.SceneTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ShrineTabletBlockEntity extends AbstractDataBlockEntity<Shrine> {
    public ShrineTabletBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.SHRINE_TABLET.get(), pos, state);
    }

    @Override
    public Shrine getNewRow() {
        return new Shrine(this);
    }

    @Override
    public Shrine getRow() {
        return BlockPartyDB.Shrines.find(this.getDatabaseID());
    }

    @Override
    public void afterUpdate() {
        BlockPos pos = this.getBlockPos().below(5);
        ServerLevel level = (ServerLevel) this.getLevel();
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        lightning.moveTo(Vec3.atBottomCenterOf(pos));
        lightning.setVisualOnly(true);
        if (level.addFreshEntity(lightning)) {
            BlockPartyNPC npc = CustomEntities.NPC.get().create(level);
            npc.absMoveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            npc.setDatabaseID(pos.asLong());
            npc.setBlockState(Blocks.BELL.defaultBlockState());
            npc.setDere(Dere.random());
            npc.claim(this.getLevel().getPlayerByUUID(this.getPlayerUUID()));
            if (level.addFreshEntity(npc)) {
                npc.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.TRIGGERED, null, null);
                npc.playSound(CustomSounds.AMBIENT_JAPAN.get(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void afterChange() {
        this.getWorld().players().stream().forEach((player) -> CustomMessenger.send(player, new SShrineList(player, this.level.dimension())));
    }
}
