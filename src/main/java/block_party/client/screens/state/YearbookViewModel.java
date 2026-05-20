package block_party.client.screens.state;

import java.util.List;

public class YearbookViewModel extends ControllerViewModel {
    public YearbookViewModel(List<Long> ids, long selectedID) {
        super(ids, selectedID);
    }

    public boolean hasNextPage() {
        return !this.ids.isEmpty() && this.index + 1 < this.ids.size();
    }

    public boolean hasPreviousPage() {
        return !this.ids.isEmpty() && this.index > 0;
    }

    public Long removeSelected() {
        Long selected = this.getSelectedID();
        if (selected == null) {
            this.close();
            return null;
        }
        this.ids.remove(this.index);
        if (this.index >= this.ids.size()) { --this.index; }
        if (this.index < 0) { this.index = 0; }
        if (this.ids.isEmpty()) {
            this.close();
            return null;
        }
        return this.getSelectedID();
    }

    public String getPageLabel() {
        return this.ids.isEmpty() ? "0/0" : String.format("%d/%d", this.index + 1, this.ids.size());
    }
}
