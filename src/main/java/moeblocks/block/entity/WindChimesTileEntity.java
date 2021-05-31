package moeblocks.block.entity;

import moeblocks.data.WindChimes;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class WindChimesTileEntity extends AbstractDataTileEntity<WindChimes> {
    public WindChimesTileEntity() {
        super(MoeTileEntities.WIND_CHIME.get());
    }

    @Override
    public WindChimes getRow() {
        return MoeWorldData.WindChimes.find(this.getDatabaseID());
    }

    @Override
    public WindChimes getNewRow() {
        return new WindChimes(this);
    }
}
