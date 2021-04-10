package moeblocks.block.entity;

import moeblocks.data.LuckyCat;
import moeblocks.init.MoeTileEntities;

public class LuckyCatTileEntity extends AbstractDataTileEntity<LuckyCat> {
    public LuckyCatTileEntity() {
        super(MoeTileEntities.LUCKY_CAT.get());
    }

    @Override
    public LuckyCat getRow() {
        return null;
    }

    @Override
    public LuckyCat getNewRow() {
        return null;
    }
}
