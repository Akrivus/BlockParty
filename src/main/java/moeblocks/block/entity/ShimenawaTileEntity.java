package moeblocks.block.entity;

import moeblocks.data.Shimenawa;
import moeblocks.init.MoeData;
import moeblocks.init.MoeTileEntities;

public class ShimenawaTileEntity extends AbstractDataTileEntity<Shimenawa> {
    public ShimenawaTileEntity() {
        super(MoeTileEntities.SHIMENAWA.get());
    }

    @Override
    public Shimenawa getRow() {
        return MoeData.Shimenawa.find(this.getDatabaseID());
    }

    @Override
    public Shimenawa getNewRow() {
        return new Shimenawa(this);
    }
}
