package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.db.records.Shrine;
import block_party.messages.SShrineList;
import block_party.registry.CustomBlockEntities;
import block_party.registry.CustomMessenger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

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
    public void afterChange() {
        this.getWorld().players().stream().forEach((player) -> CustomMessenger.send(player, new SShrineList(player, this.level.dimension())));
    }
}
