package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.custom.CustomBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ShimenawaBlockEntity extends AbstractDataBlockEntity<NPC> {
    public ShimenawaBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.SHIMENAWA.get(), pos, state);
    }

    @Override
    public NPC getRow() {
        return BlockPartyDB.NPCs.find(this.getDatabaseID());
    }

    @Override
    public NPC getNewRow() {
        return new NPC(this.getTileData());
    }
}
