package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.Supplier;

public class SpawnMobItem extends SpawnEggItem implements ISortableItem {
    private final Supplier<EntityType<?>> supplier;

    public SpawnMobItem(Supplier<EntityType<?>> supplier, int color1, int color2) {
        super(EntityType.RABBIT, color1, color2, new Properties().group(MoeMod.ITEMS));
        this.supplier = supplier;
    }

    @Override
    public EntityType<?> getType(CompoundNBT compound) {
        return supplier.get();
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
