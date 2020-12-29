package moeblocks.client.screen;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;

public class AbstractScreen extends Screen {
    protected final int white = 0xffffff;
    protected final int zero = 0;
    protected final int sizeX;
    protected final int sizeY;
    
    protected AbstractScreen(int x, int y) {
        super(NarratorChatListener.EMPTY);
        this.sizeX = x;
        this.sizeY = y;
    }
    
    public int getCenter(int size) {
        return (this.width - size) / 2;
    }
    
    public int getLeft() {
        return this.getCenter(this.sizeX);
    }
    
    public int getLeft(int margin) {
        return this.getLeft() + margin;
    }
    
    public int getRight() {
        return this.getLeft() + this.sizeX;
    }
    
    public int getRight(int margin) {
        return this.getRight() - margin;
    }
    
    public int getTop() {
        return 0;
    }
    
    public int getTop(int margin) {
        return this.getTop() + margin;
    }
    
    public int getBottom() {
        return this.height - 24;
    }
    
    public int getBottom(int margin) {
        return this.getBottom() - margin;
    }
    
    public int getAbsoluteCenter(int margin) {
        return this.getCenter(0) - margin;
    }
}
