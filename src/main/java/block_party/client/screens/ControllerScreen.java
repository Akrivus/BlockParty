package block_party.client.screens;

import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcDetailPayload;
import block_party.network.payload.NpcListPayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ControllerScreen extends Screen {
    protected final List<Long> databaseIds;
    protected int index;

    protected ControllerScreen(List<Long> databaseIds, long selectedDatabaseId) {
        super(Component.empty());
        this.databaseIds = new ArrayList<>(databaseIds);
        this.index = this.databaseIds.indexOf(selectedDatabaseId);
        if (this.index < 0) {
            this.index = 0;
        }
    }

    public void handleNpcList(NpcListPayload payload) {
    }

    public void handleNpcDetail(NpcDetailPayload payload) {
    }

    public void handleNpcCall(NpcCallPayload payload) {
    }

    protected Long selectedId() {
        return this.databaseIds.isEmpty() ? null : this.databaseIds.get(this.index);
    }

    protected int left(int width) {
        return (this.width - width) / 2;
    }

    protected int absoluteCenter(int margin) {
        return this.width / 2 - margin;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
