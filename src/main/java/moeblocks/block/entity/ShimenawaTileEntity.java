package moeblocks.block.entity;

import moeblocks.data.Shimenawa;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class ShimenawaTileEntity extends AbstractDataTileEntity<Shimenawa> {
    public ShimenawaTileEntity() {
        super(MoeTileEntities.SHIMENAWA.get());
    }

    @Override
    public Shimenawa getRow() {
        return MoeWorldData.Shimenawa.find(this.getDatabaseID());
    }

    @Override
    public Shimenawa getNewRow() {
        return new Shimenawa(this);
    }
}
