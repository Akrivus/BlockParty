package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.db.records.Gathering;
import block_party.init.BlockPartyBlockEntities;
import block_party.mob.automata.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class HangingScrollBlockEntity extends AbstractDataBlockEntity<Gathering> {
    protected Condition condition;

    public HangingScrollBlockEntity(Condition condition, BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.HANGING_SCROLL.get(), pos, state);
        this.condition = condition;
    }

    public HangingScrollBlockEntity(BlockPos pos, BlockState state) {
        this(Condition.NEVER, pos, state);
    }

    public Condition getSymbol() {
        return this.condition;
    }

    @Override
    public void load(CompoundTag compound) {
        this.condition = Condition.valueOf(compound.getString("Condition"));
        super.load(compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putString("Condition", this.condition.name());
        return super.save(compound);
    }

    @Override
    public Gathering getRow() {
        return BlockPartyDB.Gatherings.find(this.getDatabaseID());
    }

    @Override
    public Gathering getNewRow() {
        return new Gathering(this);
    }
}
