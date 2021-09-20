package block_party.blocks.entity;

import block_party.custom.CustomBlockEntities;
import block_party.npc.automata.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PaperLanternBlockEntity extends LocativeBlockEntity {
    public PaperLanternBlockEntity(BlockPos pos, BlockState state) {
        super(CustomBlockEntities.PAPER_LANTERN.get(), pos, state);
    }

    @Override
    public Condition getRequiredCondition() {
        return Condition.ALWAYS;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
