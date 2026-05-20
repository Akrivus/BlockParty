package block_party.client.screens.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControllerViewModel {
    protected final List<Long> ids;
    protected int index;
    protected boolean closed;

    public ControllerViewModel(List<Long> ids, long selectedID) {
        this.ids = new ArrayList<>(ids);
        this.index = this.ids.indexOf(selectedID);
        if (this.index < 0) { this.index = 0; }
    }

    public List<Long> getIDs() {
        return Collections.unmodifiableList(this.ids);
    }

    public int getIndex() {
        return this.index;
    }

    public int getCount() {
        return this.ids.size();
    }

    public boolean isClosed() {
        return this.closed;
    }

    public Long getSelectedID() {
        if (this.ids.isEmpty()) { return null; }
        return this.ids.get(this.index);
    }

    public Long selectIndex(int index) {
        if (this.ids.isEmpty()) {
            this.index = 0;
            return null;
        }
        if (index < 0) { index = 0; }
        if (index >= this.ids.size()) { index = this.ids.size() - 1; }
        this.index = index;
        return this.getSelectedID();
    }

    public Long next() {
        return this.selectIndex(this.index + 1);
    }

    public Long previous() {
        return this.selectIndex(this.index - 1);
    }

    public void close() {
        this.closed = true;
    }

    public void reset() {
        this.index = 0;
        this.closed = false;
    }
}
