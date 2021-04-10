package moeblocks.convo.enums;

public enum Response {
    YES, NO, CONVO, HEART, ARMOR, SKULL, CHEST, BLOCK, NEXT, CLOSE;

    public String getKey() {
        return String.format("gui.moeblocks.button.%s", this.name().toLowerCase());
    }
}
