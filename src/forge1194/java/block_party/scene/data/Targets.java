package block_party.scene.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Targets extends AbstractVariables<Integer> {
    public Targets(CompoundTag compound) {
        super(compound);
    }

    public Targets() {
        super();
    }

    @Override
    public String getKey() {
        return "Targets";
    }

    @Override
    public Integer read(CompoundTag compound) {
        return compound.getInt("Value");
    }

    @Override
    public CompoundTag write(CompoundTag compound, Integer value) {
        compound.putInt("Value", value);
        return compound;
    }

    public Entity getEntity(Level level, String name) {
        Entity entity = level.getEntity(this.get(name));
        if (entity == null)
            this.delete(name);
        return entity;
    }
}
