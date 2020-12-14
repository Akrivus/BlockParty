package moeblocks.entity;


import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public enum CupSizes {
    B(1), C(2), D(3), DD(6);

    private final int size;
    private final int rows;

    CupSizes(int rows) {
        this.size = (this.rows = rows) * 9;
    }

    public Container getContainer(int id, PlayerInventory inventory, Inventory brassiere) {
        switch (this.rows) {
        default:
            return new ChestContainer(ContainerType.GENERIC_9X1, id, inventory, brassiere, this.rows);
        case 2:
            return new ChestContainer(ContainerType.GENERIC_9X2, id, inventory, brassiere, this.rows);
        case 3:
            return new ChestContainer(ContainerType.GENERIC_9X3, id, inventory, brassiere, this.rows);
        case 4:
            return new ChestContainer(ContainerType.GENERIC_9X4, id, inventory, brassiere, this.rows);
        case 5:
            return new ChestContainer(ContainerType.GENERIC_9X5, id, inventory, brassiere, this.rows);
        case 6:
            return new ChestContainer(ContainerType.GENERIC_9X6, id, inventory, brassiere, this.rows);
        }
    }

    public static CupSizes get(int size) {
        for (CupSizes cup : CupSizes.values()) { if (cup.getSize() == size) { return cup; } }
        return CupSizes.B;
    }

    public int getSize() {
        return this.size;
    }
}