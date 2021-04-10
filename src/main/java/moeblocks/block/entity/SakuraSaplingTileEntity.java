package moeblocks.block.entity;

import moeblocks.data.SakuraSapling;
import moeblocks.init.MoeTileEntities;

public class SakuraSaplingTileEntity extends AbstractDataTileEntity<SakuraSapling> {
    public SakuraSaplingTileEntity() {
        super(MoeTileEntities.SAKURA_SAPLING.get());
    }

    @Override
    public SakuraSapling getRow() {
        return null;
    }

    @Override
    public SakuraSapling getNewRow() {
        return null;
    }
}
