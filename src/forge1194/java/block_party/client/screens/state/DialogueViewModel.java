package block_party.client.screens.state;

import block_party.scene.Response;

public class DialogueViewModel {
    private final long npcID;
    private Response response;
    private boolean closed;

    public DialogueViewModel(long npcID) {
        this.npcID = npcID;
    }

    public long getNpcID() {
        return this.npcID;
    }

    public Response getResponse() {
        return this.response;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void choose(Response response) {
        this.response = response;
    }

    public void close() {
        this.response = Response.CLOSE_DIALOGUE;
        this.closed = true;
    }

    public void reset() {
        this.response = null;
        this.closed = false;
    }
}
