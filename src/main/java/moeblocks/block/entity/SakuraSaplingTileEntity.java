package moeblocks.block.entity;

import moeblocks.data.SakuraSapling;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class SakuraSaplingTileEntity extends AbstractDataTileEntity<SakuraSapling> {
    public SakuraSaplingTileEntity() {
        super(MoeTileEntities.SAKURA_SAPLING.get());
    }

    @Override
    public SakuraSapling getRow() {
        return MoeWorldData.SakuraTrees.find(this.getDatabaseID());
    }

    @Override
    public SakuraSapling getNewRow() {
        return new SakuraSapling(this);
    }
}
