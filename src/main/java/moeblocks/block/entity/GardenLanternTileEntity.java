package moeblocks.block.entity;

import moeblocks.data.GardenLantern;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class GardenLanternTileEntity extends AbstractDataTileEntity<GardenLantern> {
    public GardenLanternTileEntity() {
        super(MoeTileEntities.GARDEN_LANTERN.get());
    }

    @Override
    public GardenLantern getRow() {
        return MoeWorldData.GardenLanterns.find(this.getDatabaseID());
    }

    @Override
    public GardenLantern getNewRow() {
        return new GardenLantern(this);
    }
}
