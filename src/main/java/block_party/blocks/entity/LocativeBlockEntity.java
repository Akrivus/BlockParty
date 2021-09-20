package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.db.records.Location;
import block_party.npc.automata.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class LocativeBlockEntity extends AbstractDataBlockEntity<Location> {
    protected LocativeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Location getRow() {
        return BlockPartyDB.Locations.find(this.getDatabaseID());
    }

    @Override
    public Location getNewRow() {
        return new Location(this);
    }

    public abstract Condition getRequiredCondition();

    public abstract int getPriority();
}
