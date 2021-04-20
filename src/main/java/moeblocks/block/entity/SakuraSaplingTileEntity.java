package moeblocks.block.entity;

import moeblocks.data.SakuraSapling;
import moeblocks.init.MoeData;
import moeblocks.init.MoeTileEntities;

public class SakuraSaplingTileEntity extends AbstractDataTileEntity<SakuraSapling> {
    public SakuraSaplingTileEntity() {
        super(MoeTileEntities.SAKURA_SAPLING.get());
    }

    @Override
    public SakuraSapling getRow() {
        return MoeData.SakuraTrees.find(this.getDatabaseID());
    }

    @Override
    public SakuraSapling getNewRow() {
        return new SakuraSapling(this);
    }
}
