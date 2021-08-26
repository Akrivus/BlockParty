package block_party.util.sort;

import block_party.db.sql.Record;
import block_party.util.DimBlockPos;

import java.util.Comparator;

public class RowDistance implements Comparator<Record> {
    private final DimBlockPos pos;

    public RowDistance(DimBlockPos pos) {
        super();
        this.pos = pos;
    }

    @Override
    public int compare(Record one, Record two) {
        DimBlockPos posOne = (DimBlockPos)(one.get(1).get());
        DimBlockPos posTwo = (DimBlockPos)(two.get(1).get());
        if (posOne.getDim().compareTo(posTwo.getDim()) == 0) {
            double d1 = this.pos.getPos().distSqr(posOne.getPos());
            double d2 = this.pos.getPos().distSqr(posTwo.getPos());
            return Double.compare(d1, d2);
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
