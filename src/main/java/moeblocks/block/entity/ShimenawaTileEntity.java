package moeblocks.block.entity;

import moeblocks.data.Shimenawa;
import moeblocks.init.MoeTileEntities;

public class ShimenawaTileEntity extends AbstractDataTileEntity<Shimenawa> {
    public ShimenawaTileEntity() {
        super(MoeTileEntities.SHIMENAWA.get());
    }

    @Override
    public Shimenawa getRow() {
        return null;
    }

    @Override
    public Shimenawa getNewRow() {
        return null;
    }
}
