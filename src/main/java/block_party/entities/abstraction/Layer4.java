package block_party.entities.abstraction;

import block_party.entities.BlockPartyNPC;
import block_party.registry.CustomTags;
import block_party.scene.traits.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Abstraction layer 4: entity data and accessors.
 */
public abstract class Layer4 extends Layer3 {
    public static final EntityDataAccessor<String> BLOOD_TYPE = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DERE = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ZODIAC = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> EMOTION = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> GENDER = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> GIVEN_NAME = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> FOOD_LEVEL = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> EXHAUSTION = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SATURATION = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> STRESS = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> RELAXATION = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LOYALTY = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AFFECTION = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SLOUCH = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AGE = SynchedEntityData.defineId(Layer4.class, EntityDataSerializers.FLOAT);

    public Layer4(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(BLOOD_TYPE, BloodType.O.getValue());
        this.entityData.define(DERE, Dere.NYANDERE.getValue());
        this.entityData.define(EMOTION, Emotion.NORMAL.getValue());
        this.entityData.define(ZODIAC, Zodiac.ARIES.getValue());
        this.entityData.define(GENDER, Gender.FEMALE.getValue());
        this.entityData.define(GIVEN_NAME, "Tokumei");
        this.entityData.define(SLOUCH, 0.0F);
        this.entityData.define(FOOD_LEVEL, 20.0F);
        this.entityData.define(EXHAUSTION, 0.0F);
        this.entityData.define(SATURATION, 6.0F);
        this.entityData.define(STRESS, 0.0F);
        this.entityData.define(RELAXATION, 0.0F);
        this.entityData.define(LOYALTY, 6.0F);
        this.entityData.define(AFFECTION, 0.0F);
        this.entityData.define(AGE, 0.0F);
        super.defineSynchedData();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data, CompoundTag compound) {
        this.setGivenName(this.getGender().getUniqueName(this.level));
        this.setBloodType(this.getBloodType().weigh(this.random));
        return super.finalizeSpawn(world, difficulty, reason, data, compound);
    }

    @Override
    public void setAdditionalBlockStateData(BlockState state) {
        if (state.is(CustomTags.HAS_MALE_PRONOUNS))
            this.setGender(Gender.MALE);
        else if (state.is(CustomTags.HAS_NONBINARY_PRONOUNS))
            this.setGender(Gender.NONBINARY);
        else if (state.is(CustomTags.HAS_FEMALE_PRONOUNS))
            this.setGender(Gender.FEMALE);

        if (state.is(CustomTags.BLOOD_TYPE_A))
            this.setBloodType(BloodType.A);
        else if (state.is(CustomTags.BLOOD_TYPE_AB))
            this.setBloodType(BloodType.AB);
        else if (state.is(CustomTags.BLOOD_TYPE_B))
            this.setBloodType(BloodType.B);
        else if (state.is(CustomTags.BLOOD_TYPE_O))
            this.setBloodType(BloodType.O);

        if (state.is(CustomTags.NYANDERE))
            this.setDere(Dere.NYANDERE);
        else if (state.is(CustomTags.HIMEDERE))
            this.setDere(Dere.HIMEDERE);
        else if (state.is(CustomTags.KUUDERE))
            this.setDere(Dere.KUUDERE);
        else if (state.is(CustomTags.TSUNDERE))
            this.setDere(Dere.TSUNDERE);
        else if (state.is(CustomTags.YANDERE))
            this.setDere(Dere.YANDERE);
        else if (state.is(CustomTags.DEREDERE))
            this.setDere(Dere.DEREDERE);
        else if (state.is(CustomTags.DANDERE))
            this.setDere(Dere.DANDERE);

        if (state.is(CustomTags.ARIES))
            this.setZodiac(Zodiac.ARIES);
        else if (state.is(CustomTags.TAURUS))
            this.setZodiac(Zodiac.TAURUS);
        else if (state.is(CustomTags.GEMINI))
            this.setZodiac(Zodiac.GEMINI);
        else if (state.is(CustomTags.CANCER))
            this.setZodiac(Zodiac.CANCER);
        else if (state.is(CustomTags.LEO))
            this.setZodiac(Zodiac.LEO);
        else if (state.is(CustomTags.VIRGO))
            this.setZodiac(Zodiac.VIRGO);
        else if (state.is(CustomTags.LIBRA))
            this.setZodiac(Zodiac.LIBRA);
        else if (state.is(CustomTags.SCORPIO))
            this.setZodiac(Zodiac.SCORPIO);
        else if (state.is(CustomTags.SAGITTARIUS))
            this.setZodiac(Zodiac.SAGITTARIUS);
        else if (state.is(CustomTags.CAPRICORN))
            this.setZodiac(Zodiac.CAPRICORN);
        else if (state.is(CustomTags.AQUARIUS))
            this.setZodiac(Zodiac.AQUARIUS);
        else if (state.is(CustomTags.PISCES))
            this.setZodiac(Zodiac.PISCES);
    }

    public BloodType getBloodType() {
        return BloodType.O.fromValue(this.entityData.get(BLOOD_TYPE));
    }

    public void setBloodType(BloodType bloodType) {
        this.entityData.set(BLOOD_TYPE, bloodType.getValue());
    }

    public Dere getDere() {
        return Dere.NYANDERE.fromValue(this.entityData.get(DERE));
    }

    public void setDere(Dere dere) {
        this.entityData.set(DERE, dere.getValue());
    }

    public Zodiac getZodiac() {
        return Zodiac.ARIES.fromValue(this.entityData.get(ZODIAC));
    }

    public void setZodiac(Zodiac zodiac) {
        this.entityData.set(ZODIAC, zodiac.getValue());
    }

    public Emotion getEmotion() {
        return Emotion.NORMAL.fromValue(this.entityData.get(EMOTION));
    }

    public void setEmotion(Emotion emotion) {
        this.entityData.set(EMOTION, emotion.getValue());
    }

    public Gender getGender() {
        return Gender.FEMALE.fromValue(this.entityData.get(GENDER));
    }

    public void setGender(Gender gender) {
        this.entityData.set(GENDER, gender.getValue());
    }

    public String getGivenName() {
        return this.entityData.get(GIVEN_NAME);
    }

    public void setGivenName(String name) {
        this.entityData.set(GIVEN_NAME, name);
    }

    public float getSlouch() {
        return this.entityData.get(SLOUCH);
    }

    public void setSlouch(float slouch) {
        this.entityData.set(SLOUCH, slouch);
    }

    public void addFoodLevel(float food_level) {
        this.setFoodLevel(this.getFoodLevel() + food_level);
    }

    public float getFoodLevel() {
        return this.entityData.get(FOOD_LEVEL);
    }

    public void setFoodLevel(float food_level) {
        this.entityData.set(FOOD_LEVEL, food_level);
    }

    public void addExhaustion(float exhaustion) {
        this.setExhaustion(this.getExhaustion() + exhaustion);
    }

    public float getExhaustion() {
        return this.entityData.get(EXHAUSTION);
    }

    public void setExhaustion(float exhaustion) {
        this.entityData.set(EXHAUSTION, exhaustion);
    }

    public void addSaturation(float saturation) {
        this.setSaturation(this.getSaturation() + saturation);
    }

    public float getSaturation() {
        return this.entityData.get(SATURATION);
    }

    public void setSaturation(float saturation) {
        this.entityData.set(SATURATION, saturation);
    }

    public void addStress(float stress) {
        this.setStress(this.getStress() + stress);
    }

    public float getStress() {
        return this.entityData.get(STRESS);
    }

    public void setStress(float stress) {
        this.entityData.set(STRESS, stress);
    }

    public void addRelaxation(float relaxation) {
        this.setRelaxation(this.getRelaxation() + relaxation);
    }

    public float getRelaxation() {
        return this.entityData.get(RELAXATION);
    }

    public void setRelaxation(float relaxation) {
        this.entityData.set(RELAXATION, relaxation);
    }

    public void addLoyalty(float loyalty) {
        this.setLoyalty(this.getLoyalty() + loyalty);
    }

    public float getLoyalty() {
        return this.entityData.get(LOYALTY);
    }

    public void setLoyalty(float loyalty) {
        this.entityData.set(LOYALTY, loyalty);
    }

    public void addAffection(float affection) {
        this.setAffection(this.getAffection() + affection);
    }

    public float getAffection() {
        return this.entityData.get(AFFECTION);
    }

    public void setAffection(float affection) {
        this.entityData.set(AFFECTION, affection);
    }

    public void addAge(float age) {
        this.setAge(this.getAge() + age);
    }

    public float getAge() {
        return this.entityData.get(AGE);
    }

    public void setAge(float age) {
        this.entityData.set(AGE, age);
    }

    public float getAgeInYears() {
        return this.getBaseAge() + this.getAge();
    }

    public String getFamilyName() {
        return "Minashigo";
    }
}
