package block_party.blocks.entity;

import block_party.init.BlockPartyBlockEntities;
import block_party.mob.automata.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimesBlockEntity extends LocativeBlockEntity {
    public WindChimesBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.WIND_CHIME.get(), pos, state);
    }

    @Override
    public Condition getRequiredCondition() {
        return Condition.NEVER;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
