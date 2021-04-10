package moeblocks.block.entity;

import moeblocks.data.ToriiGate;
import moeblocks.init.MoeTileEntities;

public class ToriiTabletTileEntity extends AbstractDataTileEntity<ToriiGate> {
    public ToriiTabletTileEntity() {
        super(MoeTileEntities.TORII_TABLET.get());
    }

    @Override
    public ToriiGate getRow() {
        return null;
    }

    @Override
    public ToriiGate getNewRow() {
        return null;
    }
}
