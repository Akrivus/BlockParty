package moeblocks.block.entity;

import moeblocks.data.WindChimes;
import moeblocks.init.MoeData;
import moeblocks.init.MoeTileEntities;

public class WindChimesTileEntity extends AbstractDataTileEntity<WindChimes> {
    public WindChimesTileEntity() {
        super(MoeTileEntities.WIND_CHIME.get());
    }

    @Override
    public WindChimes getRow() {
        return MoeData.WindChimes.find(this.getDatabaseID());
    }

    @Override
    public WindChimes getNewRow() {
        return new WindChimes(this);
    }
}
