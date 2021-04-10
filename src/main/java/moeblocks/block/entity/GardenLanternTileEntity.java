package moeblocks.block.entity;

import moeblocks.data.GardenLantern;
import moeblocks.init.MoeTileEntities;

public class GardenLanternTileEntity extends AbstractDataTileEntity<GardenLantern> {
    public GardenLanternTileEntity() {
        super(MoeTileEntities.GARDEN_LANTERN.get());
    }

    @Override
    public GardenLantern getRow() {
        return null;
    }

    @Override
    public GardenLantern getNewRow() {
        return null;
    }
}
