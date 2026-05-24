package block_party.client;

import block_party.client.screens.CellPhoneScreen;
import block_party.client.screens.ControllerScreen;
import block_party.client.screens.DialogueScreen;
import block_party.client.screens.YearbookScreen;
import block_party.network.payload.ControllerOpenPayload;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.ShrineListPayload;
import net.minecraft.client.Minecraft;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {
    }

    public static void openDialogue(DialogueOpenPayload payload) {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().screen instanceof CellPhoneScreen screen) {
                screen.openDialogueAfterClosing(payload);
                return;
            }
            Minecraft.getInstance().setScreen(new DialogueScreen(payload));
        });
    }

    public static void openController(ControllerOpenPayload payload) {
        Minecraft.getInstance().execute(() -> {
            ControllerScreen screen = switch (payload.controller()) {
                case CELL_PHONE -> new CellPhoneScreen(payload.npcs());
                case YEARBOOK -> new YearbookScreen(payload.npcs(), payload.selectedDatabaseId());
            };
            Minecraft.getInstance().setScreen(screen);
        });
    }

    public static void handleNpcCall(NpcCallPayload payload) {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().screen instanceof ControllerScreen screen) {
                screen.handleNpcCall(payload);
            }
        });
    }

    public static void handleShrineList(ShrineListPayload payload) {
        Minecraft.getInstance().execute(() -> ShrineLocation.update(payload.positions()));
    }
}
