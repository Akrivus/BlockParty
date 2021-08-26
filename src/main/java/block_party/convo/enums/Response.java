package block_party.convo.enums;

public enum Response {
    YES, NO, CONVO, HEART, ARMOR, SKULL, CHEST, BLOCK, NEXT, CLOSE;

    public String getKey() {
        return String.format("gui.block_party.button.%s", this.name().toLowerCase());
    }
}
