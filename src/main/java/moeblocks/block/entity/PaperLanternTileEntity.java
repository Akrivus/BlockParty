package moeblocks.block.entity;

import moeblocks.data.PaperLantern;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class PaperLanternTileEntity extends AbstractDataTileEntity<PaperLantern> {
    public PaperLanternTileEntity() {
        super(MoeTileEntities.PAPER_LANTERN.get());
    }

    @Override
    public PaperLantern getRow() {
        return MoeWorldData.PaperLanterns.find(this.getDatabaseID());
    }

    @Override
    public PaperLantern getNewRow() {
        return new PaperLantern(this);
    }
}
