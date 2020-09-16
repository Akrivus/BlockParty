package moe.blocks.mod.data.yearbook;

import moe.blocks.mod.data.dating.Relationship;
import moe.blocks.mod.entity.ai.BloodTypes;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.init.MoeEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;

import java.util.UUID;

public class Page {
    private final CompoundNBT character;
    private final UUID uuid;

    public Page(INBT compound) {
        this.character = (CompoundNBT) compound;
        this.uuid = this.character.getUniqueId("PageUUID");
    }

    public Page(CharacterEntity entity, UUID uuid) {
        entity.setYearbookPage(this.character = new CompoundNBT(), uuid);
        this.uuid = entity.getUniqueID();
    }

    public CompoundNBT write() {
        this.character.putUniqueId("PageUUID", this.uuid);
        return this.character;
    }

    public CharacterEntity getCharacter(Minecraft minecraft) {
        CharacterEntity character = MoeEntities.MOE.get().create(minecraft.world);
        character.readAdditional(this.character);
        character.setPosition(minecraft.player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ());
        character.rotationYaw = 0.75F * -(character.rotationYawHead = 180.0F);
        if (this.getDere() == Deres.YANDERE) { character.setEmotion(Emotions.PSYCHOTIC, 0); }
        if (this.getDere() == Deres.DANDERE) { character.setEmotion(Emotions.HAPPY, 0); }
        character.isInYearbook = true;
        return character;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName(CharacterEntity character) {
        return character.getFullName();
    }

    public float getHealth() {
        return this.character.getFloat("Health");
    }

    public float getHunger() {
        return this.character.getFloat("Hunger");
    }

    public float getLove() {
        return this.character.getFloat("Love");
    }

    public float getStress() {
        return this.character.getFloat("Stress");
    }

    public Deres getDere() {
        return Deres.valueOf(this.character.getString("Dere"));
    }

    public Relationship.Status getStatus() {
        return Relationship.Status.valueOf(this.character.getString("Status"));
    }

    public BloodTypes getBloodType() {
        return BloodTypes.valueOf(this.character.getString("BloodType"));
    }

    public int getAge() {
        return this.character.getInt("AgeInYears");
    }
}
