package moeblocks.block.entity;

import moeblocks.data.PaperLantern;
import moeblocks.init.MoeTileEntities;

public class PaperLanternTileEntity extends AbstractDataTileEntity<PaperLantern> {
    public PaperLanternTileEntity() {
        super(MoeTileEntities.PAPER_LANTERN.get());
    }

    @Override
    public PaperLantern getRow() {
        return null;
    }

    @Override
    public PaperLantern getNewRow() {
        return null;
    }
}
