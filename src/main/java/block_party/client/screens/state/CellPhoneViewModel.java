package block_party.client.screens.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CellPhoneViewModel extends ControllerViewModel {
    private final List<Long> contacts = new ArrayList<>();
    private int total;
    private int start;

    public CellPhoneViewModel(List<Long> ids) {
        super(ids, -1);
    }

    public void addContact(long id) {
        this.contacts.add(id);
    }

    public void markResponseLoaded() {
        ++this.total;
        if (this.total >= this.ids.size() && this.contacts.isEmpty()) {
            this.close();
        }
    }

    public List<Long> getVisibleContacts() {
        if (this.contacts.isEmpty()) { return Collections.emptyList(); }
        int end = Math.min(this.start + 4, this.contacts.size());
        return Collections.unmodifiableList(this.contacts.subList(this.start, end));
    }

    public int getStart() {
        return this.start;
    }

    public void scroll(int delta) {
        if (this.contacts.isEmpty()) {
            this.start = 0;
            return;
        }
        this.start += 4 * delta;
        int range = this.contacts.size() - 1;
        if (this.start < 0) { this.start = range - range % 4; }
        if (this.start > range) { this.start = 0; }
    }
}
