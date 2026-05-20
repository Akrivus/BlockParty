package block_party.regression;

import block_party.db.records.NPC;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import block_party.scene.traits.BloodType;
import block_party.scene.traits.Dere;
import block_party.scene.traits.Emotion;
import block_party.scene.traits.Gender;
import block_party.scene.traits.Zodiac;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertFalse;
import static block_party.regression.TestSupport.assertTrait;
import static block_party.regression.TestSupport.assertTrue;

final class TraitRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testTraitParsingAndValues();
        testInvalidTraitFallback();
        testSerializedTraitIdRoundTrip();
        testOldTraitNamesHaveNoAliasesAndFallbackToReceiver();
        testDuplicateTraitWritesUseLastValue();
        testMissingTraitFieldsUseReceiverDefaults();
        testStoredNpcRowsPreserveTraitColumns();
        testBloodTypeCompatibility();
    }

    private void testTraitParsingAndValues() {
        assertTrait(Gender.FEMALE, Gender.MALE.fromValue("female"), "Gender parses lowercase values");
        assertTrait(Gender.NONBINARY, Gender.NONBINARY.fromValue("unknown"), "Gender falls back to receiver");
        assertEquals("kun", Gender.MALE.getHonorific(), "Male honorific");
        assertEquals("chan", Gender.FEMALE.getHonorific(), "Female honorific");
        assertEquals("kun", Gender.NONBINARY.getHonorific(), "Nonbinary honorific");

        assertTrait(Dere.YANDERE, Dere.DANDERE.fromValue("yandere"), "Dere parses lowercase values");
        assertTrait(Dere.KUUDERE, Dere.KUUDERE.fromValue("unknown"), "Dere falls back to receiver");

        assertTrait(Emotion.SMITTEN, Emotion.NORMAL.fromValue("smitten"), "Emotion parses lowercase values");
        assertTrait(Emotion.NORMAL, Emotion.NORMAL.fromValue("unknown"), "Emotion falls back to receiver");

        assertTrait(Zodiac.PISCES, Zodiac.ARIES.fromValue("pisces"), "Zodiac parses lowercase values");
        assertTrait(Zodiac.CANCER, Zodiac.CANCER.fromValue("unknown"), "Zodiac falls back to receiver");
    }

    private void testInvalidTraitFallback() {
        assertTrait(Gender.MALE, Gender.MALE.fromValue("bad id"), "Invalid gender falls back to receiver");
        assertTrait(BloodType.A, BloodType.A.fromValue("bad id"), "Invalid blood type falls back to receiver");
        assertTrait(Dere.DEREDERE, Dere.DEREDERE.fromValue("bad id"), "Invalid dere falls back to receiver");
        assertTrait(Emotion.HAPPY, Emotion.HAPPY.fromValue("bad id"), "Invalid emotion falls back to receiver");
        assertTrait(Zodiac.LEO, Zodiac.LEO.fromValue("bad id"), "Invalid zodiac falls back to receiver");
    }

    private void testSerializedTraitIdRoundTrip() {
        CompoundTag tag = new CompoundTag();
        Gender.NONBINARY.write(tag);
        BloodType.AB.write(tag);
        Dere.TSUNDERE.write(tag);
        Emotion.SCARED.write(tag);
        Zodiac.CAPRICORN.write(tag);

        assertTrait(Gender.NONBINARY, Gender.FEMALE.read(tag), "Gender NBT round trip");
        assertTrait(BloodType.AB, BloodType.O.read(tag), "Blood type NBT round trip");
        assertTrait(Dere.TSUNDERE, Dere.NYANDERE.read(tag), "Dere NBT round trip");
        assertTrait(Emotion.SCARED, Emotion.NORMAL.read(tag), "Emotion NBT round trip");
        assertTrait(Zodiac.CAPRICORN, Zodiac.ARIES.read(tag), "Zodiac NBT round trip");
        assertEquals("NONBINARY", tag.getString("Gender"), "Serialized gender ID");
        assertEquals("AB", tag.getString("BloodType"), "Serialized blood type ID");
        assertEquals("TSUNDERE", tag.getString("Dere"), "Serialized dere ID");
        assertEquals("SCARED", tag.getString("Emotion"), "Serialized emotion ID");
        assertEquals("CAPRICORN", tag.getString("Zodiac"), "Serialized zodiac ID");
    }

    private void testOldTraitNamesHaveNoAliasesAndFallbackToReceiver() {
        assertTrait(Gender.FEMALE, Gender.FEMALE.fromValue("non_binary"), "Old-style non_binary gender alias is not currently supported");
        assertTrait(BloodType.O, BloodType.O.fromValue("type_o"), "Old-style type_o blood alias is not currently supported");
        assertTrait(Dere.NYANDERE, Dere.NYANDERE.fromValue("nya_dere"), "Old-style dere alias is not currently supported");
        assertTrait(Emotion.NORMAL, Emotion.NORMAL.fromValue("very_happy"), "Old-style emotion alias is not currently supported");
        assertTrait(Zodiac.ARIES, Zodiac.ARIES.fromValue("sea_goat"), "Old-style zodiac alias is not currently supported");
    }

    private void testDuplicateTraitWritesUseLastValue() {
        CompoundTag tag = new CompoundTag();
        Gender.MALE.write(tag);
        Gender.FEMALE.write(tag);
        BloodType.A.write(tag);
        BloodType.B.write(tag);
        Dere.KUUDERE.write(tag);
        Dere.YANDERE.write(tag);
        Emotion.ANGRY.write(tag);
        Emotion.TIRED.write(tag);
        Zodiac.ARIES.write(tag);
        Zodiac.PISCES.write(tag);

        assertTrait(Gender.FEMALE, Gender.MALE.read(tag), "Duplicate gender writes use last value");
        assertTrait(BloodType.B, BloodType.O.read(tag), "Duplicate blood type writes use last value");
        assertTrait(Dere.YANDERE, Dere.NYANDERE.read(tag), "Duplicate dere writes use last value");
        assertTrait(Emotion.TIRED, Emotion.NORMAL.read(tag), "Duplicate emotion writes use last value");
        assertTrait(Zodiac.PISCES, Zodiac.ARIES.read(tag), "Duplicate zodiac writes use last value");
    }

    private void testMissingTraitFieldsUseReceiverDefaults() {
        CompoundTag tag = new CompoundTag();

        assertTrait(Gender.FEMALE, Gender.FEMALE.read(tag), "Missing gender field uses receiver default");
        assertTrait(BloodType.O, BloodType.O.read(tag), "Missing blood type field uses receiver default");
        assertTrait(Dere.NYANDERE, Dere.NYANDERE.read(tag), "Missing dere field uses receiver default");
        assertTrait(Emotion.NORMAL, Emotion.NORMAL.read(tag), "Missing emotion field uses receiver default");
        assertTrait(Zodiac.ARIES, Zodiac.ARIES.read(tag), "Missing zodiac field uses receiver default");
    }

    private void testStoredNpcRowsPreserveTraitColumns() {
        NPC npc = new NPC(npcTag());
        NPC restored = new NPC(npc.write());

        assertTrait(BloodType.O, (BloodType) restored.get(NPC.BLOOD_TYPE).get(), "Stored NPC row preserves blood type");
        assertTrait(Dere.NYANDERE, (Dere) restored.get(NPC.DERE).get(), "Stored NPC row preserves dere");
    }

    private void testBloodTypeCompatibility() {
        assertTrait(BloodType.AB, BloodType.O.fromValue("ab"), "Blood type parses lowercase values");
        assertTrait(BloodType.B, BloodType.B.fromValue("unknown"), "Blood type falls back to receiver");
        assertEquals(1, BloodType.AB.getWeight(), "AB weight");
        assertEquals(3, BloodType.B.getWeight(), "B weight");
        assertEquals(5, BloodType.A.getWeight(), "A weight");
        assertEquals(7, BloodType.O.getWeight(), "O weight");

        assertTrue(BloodType.O.isCompatible(BloodType.A), "O is compatible with A");
        assertTrue(BloodType.O.isCompatible(BloodType.B), "O is compatible with B");
        assertTrue(BloodType.O.isCompatible(BloodType.AB), "O is compatible with AB");
        assertTrue(BloodType.A.isCompatible(BloodType.AB), "A is compatible with AB");
        assertTrue(BloodType.B.isCompatible(BloodType.AB), "B is compatible with AB");
        assertTrue(BloodType.AB.isCompatible(BloodType.AB), "AB is compatible with itself");
        assertFalse(BloodType.AB.isCompatible(BloodType.O), "AB is not compatible with O");
        assertFalse(BloodType.A.isCompatible(BloodType.B), "A is not compatible with B");
    }

    private CompoundTag npcTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DatabaseID", 200L);
        tag.put("PosDim", dimPosTag("minecraft:overworld", 0, 64, 0, false));
        tag.putUUID("PlayerUUID", new java.util.UUID(0L, 0L));
        tag.putBoolean("Dead", false);
        tag.putString("Name", "TraitRow");
        tag.putString("BloodType", "O");
        tag.putString("Dere", "NYANDERE");
        tag.putFloat("Health", 20.0F);
        tag.putFloat("FoodLevel", 20.0F);
        tag.putFloat("Exhaustion", 0.0F);
        tag.putFloat("Saturation", 6.0F);
        tag.putFloat("Stress", 0.0F);
        tag.putFloat("Relaxation", 0.0F);
        tag.putFloat("Loyalty", 6.0F);
        tag.putFloat("Affection", 0.0F);
        tag.putFloat("Slouch", 0.0F);
        tag.putFloat("Age", 0.0F);
        tag.putLong("LastSeenAt", 0L);
        tag.putInt("BlockState", 0);
        tag.putBoolean("Hiding", false);
        tag.putBoolean("HasHome", false);
        tag.put("HomePosDim", dimPosTag("minecraft:overworld", 0, 0, 0, true));
        return tag;
    }

    private CompoundTag dimPosTag(String dim, int x, int y, int z, boolean empty) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Coordinates", new BlockPos(x, y, z).asLong());
        tag.putString("Dimension", dim);
        tag.putBoolean("IsEmpty", empty);
        return tag;
    }
}
