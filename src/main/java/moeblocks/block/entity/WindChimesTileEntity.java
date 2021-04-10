package moeblocks.block.entity;

import moeblocks.data.WindChimes;
import moeblocks.init.MoeTileEntities;

public class WindChimesTileEntity extends AbstractDataTileEntity<WindChimes> {
    public WindChimesTileEntity() {
        super(MoeTileEntities.WIND_CHIME.get());
    }

    @Override
    public WindChimes getRow() {
        return null;
    }

    @Override
    public WindChimes getNewRow() {
        return null;
    }
}
