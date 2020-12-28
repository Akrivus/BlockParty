package moeblocks.datingsim.convo.enums;

public enum Response {
    YES,
    NO,
    CONVO,
    HEART,
    ARMOR,
    SKULL,
    NEXT,
    CLOSE;
    
    public String getKey() {
        return String.format("gui.moeblocks.button.%s", this.name().toLowerCase());
    }
}
