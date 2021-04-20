package moeblocks.block.entity;

import moeblocks.data.ToriiGate;
import moeblocks.init.MoeData;
import moeblocks.init.MoeTileEntities;

public class ToriiTabletTileEntity extends AbstractDataTileEntity<ToriiGate> {
    public ToriiTabletTileEntity() {
        super(MoeTileEntities.TORII_TABLET.get());
    }

    @Override
    public ToriiGate getRow() {
        return MoeData.ToriiGates.find(this.getDatabaseID());
    }

    @Override
    public ToriiGate getNewRow() {
        return new ToriiGate(this);
    }
}
