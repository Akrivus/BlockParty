package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ShimenawaBlockEntity extends AbstractDataBlockEntity<NPC> {
    public ShimenawaBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.SHIMENAWA.get(), pos, state);
    }

    @Override
    public NPC getRow() {
        return BlockPartyDB.NPCs.find(this.getDatabaseID());
    }

    @Override
    public NPC getNewRow() {
        return new NPC(this);
    }
}
