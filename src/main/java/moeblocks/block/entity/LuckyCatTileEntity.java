package moeblocks.block.entity;

import moeblocks.data.LuckyCat;
import moeblocks.init.MoeData;
import moeblocks.init.MoeTileEntities;

public class LuckyCatTileEntity extends AbstractDataTileEntity<LuckyCat> {
    public LuckyCatTileEntity() {
        super(MoeTileEntities.LUCKY_CAT.get());
    }

    @Override
    public LuckyCat getRow() {
        return MoeData.LuckyCats.find(this.getDatabaseID());
    }

    @Override
    public LuckyCat getNewRow() {
        return new LuckyCat(this);
    }
}
