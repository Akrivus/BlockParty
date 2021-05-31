package moeblocks.block.entity;

import moeblocks.data.ToriiGate;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class ToriiTabletTileEntity extends AbstractDataTileEntity<ToriiGate> {
    public ToriiTabletTileEntity() {
        super(MoeTileEntities.TORII_TABLET.get());
    }

    @Override
    public ToriiGate getRow() {
        return MoeWorldData.ToriiGates.find(this.getDatabaseID());
    }

    @Override
    public ToriiGate getNewRow() {
        return new ToriiGate(this);
    }
}
