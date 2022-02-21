package block_party.entities;

import block_party.entities.abstraction.Layer7;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class BlockPartyNPC extends Layer7 {
    public BlockPartyNPC(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
        this.setBloodType(this.getBloodType().weigh(this.random));
    }

    @Override
    public Component getTypeName() {
        return new TranslatableComponent("entity.block_party.profession", this.getGivenName(), this.getFamilyName());
    }

    @Override
    public boolean hasCustomName() { return true; }

    @Override
    public Component getCustomName() {
        return new TranslatableComponent("entity.block_party.generic", this.getGivenName(), this.getHonorific());
    }

    public String getHonorific() {
        return this.getGender().getHonorific();
    }
}