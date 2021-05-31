package moeblocks.block.entity;

import moeblocks.data.LuckyCat;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class LuckyCatTileEntity extends AbstractDataTileEntity<LuckyCat> {
    public LuckyCatTileEntity() {
        super(MoeTileEntities.LUCKY_CAT.get());
    }

    @Override
    public LuckyCat getRow() {
        return MoeWorldData.LuckyCats.find(this.getDatabaseID());
    }

    @Override
    public LuckyCat getNewRow() {
        return new LuckyCat(this);
    }
}
