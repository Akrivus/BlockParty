package block_party.client.screens;

import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcDetailPayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ControllerScreen extends Screen {
    protected final List<NpcDetailPayload> npcs;
    protected int index;

    protected ControllerScreen(List<NpcDetailPayload> npcs, long selectedDatabaseId) {
        super(Component.empty());
        this.npcs = new ArrayList<>(npcs);
        this.index = selectedIndex(selectedDatabaseId);
        if (this.index < 0) {
            this.index = 0;
        }
    }

    public void handleNpcCall(NpcCallPayload payload) {
    }

    protected NpcDetailPayload selectedNpc() {
        return this.npcs.isEmpty() ? null : this.npcs.get(this.index);
    }

    protected Long selectedId() {
        NpcDetailPayload selected = this.selectedNpc();
        return selected == null ? null : selected.databaseId();
    }

    private int selectedIndex(long selectedDatabaseId) {
        for (int index = 0; index < this.npcs.size(); ++index) {
            if (this.npcs.get(index).databaseId() == selectedDatabaseId) {
                return index;
            }
        }
        return -1;
    }

    protected int left(int width) {
        return (this.width - width) / 2;
    }

    protected int absoluteCenter(int margin) {
        return this.width / 2 - margin;
    }

    protected void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
