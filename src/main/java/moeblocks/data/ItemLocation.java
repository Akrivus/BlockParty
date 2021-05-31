package moeblocks.data;

import moeblocks.data.sql.Column;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeWorldData;
import moeblocks.util.DimBlockPos;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ItemLocation extends Row<ItemLocation> implements IModelEntity<ItemLocation> {
    private UUID databaseID;
    private UUID playerUUID;
    private World world;
    private DimBlockPos pos;
    private Item item;
    private int count;
    private int slot;

    public ItemLocation(ResultSet set) throws SQLException {
        super(MoeWorldData.ItemLocations, set);
    }

    public ItemLocation(CompoundNBT compound) {
        super(MoeWorldData.ItemLocations, compound);
    }

    public ItemLocation(World world, DimBlockPos pos, ItemStack stack, int slot) {
        super(MoeWorldData.ItemLocations);
        this.world = world;
        this.pos = pos;
        this.item = stack.getItem();
        this.count = stack.getCount();
        this.slot = slot;
    }

    @Override
    public void sync(ItemLocation entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(ItemLocation entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public DimBlockPos getDimBlockPos() {
        return this.pos;
    }

    public void setDimBlockPos(DimBlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void setDatabaseID(UUID uuid) {
        this.databaseID = uuid;
    }

    @Override
    public UUID getDatabaseID() {
        return this.databaseID;
    }

    @Override
    public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid;
    }

    @Override
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public ItemStack getStack() {
        return new ItemStack(this.item, this.count);
    }

    public void setStack(ItemStack stack) {
        this.item = stack.getItem();
        this.count = stack.getCount();
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public boolean hasRow() {
        return true;
    }

    @Override
    public ItemLocation getRow() {
        return MoeWorldData.ItemLocations.find(this.getDatabaseID());
    }

    @Override
    public ItemLocation getNewRow() {
        return this;
    }

    public static class Schema extends Table<ItemLocation> {
        public Schema() {
            super("ItemLocations");
            this.addColumn(new Column.AsItem(this, "Item"));
            this.addColumn(new Column.AsInteger(this, "Count"));
            this.addColumn(new Column.AsReference<>(this, "ToriiGate", (uuid) -> MoeWorldData.ToriiGates.find(uuid)));
        }

        @Override
        public ItemLocation getRow(ResultSet set) throws SQLException {
            return new ItemLocation(set);
        }
    }
}
