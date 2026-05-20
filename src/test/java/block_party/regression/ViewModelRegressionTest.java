package block_party.regression;

import block_party.client.screens.state.CellPhoneViewModel;
import block_party.client.screens.state.ControllerViewModel;
import block_party.client.screens.state.DialogueViewModel;
import block_party.client.screens.state.YearbookViewModel;
import block_party.messages.CDialogueRespond;
import block_party.scene.Response;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;
import java.util.Collections;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertFalse;
import static block_party.regression.TestSupport.assertNull;
import static block_party.regression.TestSupport.assertTrue;
import static block_party.regression.TestSupport.getField;

final class ViewModelRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testDefaultControllerStateConstruction();
        testSelectionChangesClampToBounds();
        testEmptyListBehavior();
        testYearbookRemoveSelectedClosesOnLastPage();
        testCellPhoneNavigationBoundsAndEmptyContacts();
        testDialogueResponseChoiceAndCloseReset();
        testDialogueActionPayloadRoundTrip();
    }

    private void testDefaultControllerStateConstruction() {
        ControllerViewModel model = new ControllerViewModel(Arrays.asList(10L, 20L, 30L), 20L);

        assertEquals(1, model.getIndex(), "Controller selected index");
        assertEquals(3, model.getCount(), "Controller count");
        assertEquals(20L, model.getSelectedID(), "Controller selected ID");
        assertFalse(model.isClosed(), "Controller starts open");
    }

    private void testSelectionChangesClampToBounds() {
        YearbookViewModel model = new YearbookViewModel(Arrays.asList(1L, 2L, 3L), 1L);

        assertEquals(1L, model.selectIndex(-1), "Selection clamps low");
        assertEquals(3L, model.selectIndex(99), "Selection clamps high");
        assertEquals(2L, model.previous(), "Previous moves back");
        assertEquals(3L, model.next(), "Next moves forward");
        assertTrue(model.hasPreviousPage(), "Has previous page");
        assertFalse(model.hasNextPage(), "No next page at end");
    }

    private void testEmptyListBehavior() {
        YearbookViewModel model = new YearbookViewModel(Collections.emptyList(), -1L);

        assertEquals(0, model.getIndex(), "Empty list index");
        assertNull(model.getSelectedID(), "Empty list selected ID");
        assertEquals("0/0", model.getPageLabel(), "Empty list page label");
        assertNull(model.removeSelected(), "Removing selected from empty list returns null");
        assertTrue(model.isClosed(), "Removing selected from empty list closes");
    }

    private void testYearbookRemoveSelectedClosesOnLastPage() {
        YearbookViewModel model = new YearbookViewModel(Arrays.asList(4L, 5L), 5L);

        assertEquals(4L, model.removeSelected(), "Removing stale selected page selects previous remaining page");
        assertEquals("1/1", model.getPageLabel(), "Page label after removal");
        assertEquals(null, model.removeSelected(), "Removing last page returns null");
        assertTrue(model.isClosed(), "Removing last page closes");
    }

    private void testCellPhoneNavigationBoundsAndEmptyContacts() {
        CellPhoneViewModel model = new CellPhoneViewModel(Arrays.asList(1L, 2L, 3L, 4L, 5L));

        assertEquals(Collections.emptyList(), model.getVisibleContacts(), "Empty contacts are visible as empty list");
        model.scroll(1);
        assertEquals(0, model.getStart(), "Scrolling empty contacts keeps start at zero");
        model.addContact(1L);
        model.addContact(2L);
        model.addContact(3L);
        model.addContact(4L);
        model.addContact(5L);
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L), model.getVisibleContacts(), "First phone page");
        model.scroll(1);
        assertEquals(Collections.singletonList(5L), model.getVisibleContacts(), "Second phone page");
        model.scroll(1);
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L), model.getVisibleContacts(), "Phone page wraps high");

        CellPhoneViewModel emptyAfterLoads = new CellPhoneViewModel(Arrays.asList(7L, 8L));
        emptyAfterLoads.markResponseLoaded();
        assertFalse(emptyAfterLoads.isClosed(), "Phone remains open until all empty responses loaded");
        emptyAfterLoads.markResponseLoaded();
        assertTrue(emptyAfterLoads.isClosed(), "Phone closes when all responses are stale/removed");
    }

    private void testDialogueResponseChoiceAndCloseReset() {
        DialogueViewModel model = new DialogueViewModel(44L);

        assertNull(model.getResponse(), "Dialogue starts without response");
        model.choose(Response.LOVELY_HEART);
        assertEquals(Response.LOVELY_HEART, model.getResponse(), "Dialogue stores chosen response");
        model.close();
        assertEquals(Response.CLOSE_DIALOGUE, model.getResponse(), "Dialogue close stores close response");
        assertTrue(model.isClosed(), "Dialogue close marks closed");
        model.reset();
        assertNull(model.getResponse(), "Dialogue reset clears response");
        assertFalse(model.isClosed(), "Dialogue reset reopens state");
    }

    private void testDialogueActionPayloadRoundTrip() {
        DialogueViewModel model = new DialogueViewModel(55L);
        model.choose(Response.TRUSTY_ARMOR);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        new CDialogueRespond(model.getNpcID(), model.getResponse()).encode(buffer);
        CDialogueRespond decoded = new CDialogueRespond(buffer);

        assertEquals(55L, getField(decoded, "id"), "Dialogue view-model action payload NPC ID");
        assertEquals(Response.TRUSTY_ARMOR, getField(decoded, "response"), "Dialogue view-model action payload response");
    }
}
