package moeblocks.data;

import moeblocks.automata.trait.BloodType;
import moeblocks.automata.trait.Dere;
import moeblocks.data.sql.Column;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractNPC<E extends AbstractNPCEntity> extends Row<E> {
    protected static final int DEAD         =  6;
    protected static final int NAME         =  7;
    protected static final int BLOOD_TYPE   =  8;
    protected static final int DERE         =  9;
    protected static final int HEALTH       = 10;
    protected static final int FULLNESS     = 11;
    protected static final int EXHAUSTION   = 12;
    protected static final int SATURATION   = 13;
    protected static final int STRESS       = 14;
    protected static final int RELAXATION   = 15;
    protected static final int LOYALTY      = 16;
    protected static final int AFFECTION    = 17;
    protected static final int SLOUCH       = 18;
    protected static final int AGE          = 19;
    protected static final int LAST_SEEN_AT = 20;

    protected AbstractNPC(Table table, ResultSet set) throws SQLException {
        super(table, set);
    }

    protected AbstractNPC(Table table, CompoundNBT compound) {
        super(table, compound);
    }

    protected AbstractNPC(Table table, E entity) {
        super(table, entity);
    }

    public String getName() {
        return (String) this.get(NAME).get();
    }

    public boolean isDead() {
        return (Boolean) this.get(DEAD).get();
    }

    public boolean isEstrangedFrom(PlayerEntity player) {
        return !player.getUniqueID().equals(this.get(PLAYER_UUID).get());
    }

    public boolean isDeadOrEstrangedFrom(PlayerEntity player) {
        return this.isDead() || this.isEstrangedFrom(player);
    }

    public abstract E getServerEntity(MinecraftServer server);

    public abstract E getClientEntity(Minecraft client);

    public static AbstractNPC create(CompoundNBT compound) {
        return new Moe(compound);
    }

    public static abstract class Schema<NPC extends AbstractNPC> extends Table<NPC> {
        public Schema(String name) {
            super(name);
            this.addColumn(new Column.AsBoolean(this, "Dead"));
            this.addColumn(new Column.AsString(this, "Name"));
            this.addColumn(new Column.AsTrait<>(this, "BloodType", BloodType.O));
            this.addColumn(new Column.AsTrait<>(this, "Dere", Dere.NYANDERE));
            this.addColumn(new Column.AsFloat(this, "Health"));
            this.addColumn(new Column.AsFloat(this, "Fullness"));
            this.addColumn(new Column.AsFloat(this, "Exhaustion"));
            this.addColumn(new Column.AsFloat(this, "Saturation"));
            this.addColumn(new Column.AsFloat(this, "Stress"));
            this.addColumn(new Column.AsFloat(this, "Relaxation"));
            this.addColumn(new Column.AsFloat(this, "Loyalty"));
            this.addColumn(new Column.AsFloat(this, "Affection"));
            this.addColumn(new Column.AsFloat(this, "Slouch"));
            this.addColumn(new Column.AsFloat(this, "Age"));
            this.addColumn(new Column.AsLong(this, "LastSeenAt"));
        }
    }
}
