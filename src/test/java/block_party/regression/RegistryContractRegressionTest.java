package block_party.regression;

import block_party.scene.Dialogue;
import block_party.scene.Speaker;
import block_party.scene.traits.Emotion;
import block_party.registry.resources.MoeSounds;
import block_party.utils.JsonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertNull;
import static block_party.regression.TestSupport.assertTrait;

final class RegistryContractRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testKnownVanillaSoundRegistryKeyIsStable();
        testEmotionParsingDoesNotTouchSoundRegistry();
        testEmotionSoundNamesMatchLegacyMapping();
        testSpeakerKnownVoiceIdRoundTripsWithoutResolution();
        testSpeakerUnknownVoiceIdRoundTripsWithoutResolution();
        testSpeakerInvalidVoiceIdFallsBackSafely();
        testDialogueKnownSoundIdRoundTripsWithoutResolution();
        testDialogueRenamedOrMissingSoundIdPreservesSerializedId();
        testDialogueMissingSoundFieldDoesNotCrashPureParsing();
        testJsonUtilsKnownRegistryLookupReturnsNullKnownBug();
    }

    private void testKnownVanillaSoundRegistryKeyIsStable() {
        assertEquals(new ResourceLocation("minecraft:block.note_block.bell"), SoundEvents.NOTE_BLOCK_BELL.value().getLocation(), "Known vanilla sound key remains stable");
    }

    private void testEmotionParsingDoesNotTouchSoundRegistry() {
        assertTrait(Emotion.SMITTEN, Emotion.NORMAL.fromValue("smitten"), "Emotion parses without live mod registries");
        assertTrait(Emotion.NORMAL, Emotion.NORMAL.fromValue("unknown_emotion"), "Unknown emotion falls back safely");
        assertEquals("SMITTEN", Emotion.SMITTEN.getValue(), "Emotion serialized value stays stable");
    }

    private void testEmotionSoundNamesMatchLegacyMapping() {
        assertEquals("angry", MoeSounds.getEmotionSoundName(Emotion.ANGRY), "Angry emotion sound");
        assertEquals("neutral", MoeSounds.getEmotionSoundName(Emotion.BEGGING), "Begging emotion sound");
        assertEquals("confused", MoeSounds.getEmotionSoundName(Emotion.CONFUSED), "Confused emotion sound");
        assertEquals("crying", MoeSounds.getEmotionSoundName(Emotion.CRYING), "Crying emotion sound");
        assertEquals("snicker", MoeSounds.getEmotionSoundName(Emotion.MISCHIEVOUS), "Mischievous emotion sound");
        assertEquals("attack", MoeSounds.getEmotionSoundName(Emotion.EMBARRASSED), "Embarrassed emotion sound");
        assertEquals("giggle", MoeSounds.getEmotionSoundName(Emotion.HAPPY), "Happy emotion sound");
        assertEquals("laugh", MoeSounds.getEmotionSoundName(Emotion.NORMAL), "Normal emotion sound");
        assertEquals("attack", MoeSounds.getEmotionSoundName(Emotion.PAINED), "Pained emotion sound");
        assertEquals("psychotic", MoeSounds.getEmotionSoundName(Emotion.PSYCHOTIC), "Psychotic emotion sound");
        assertEquals("crying", MoeSounds.getEmotionSoundName(Emotion.SCARED), "Scared emotion sound");
        assertEquals("sneeze", MoeSounds.getEmotionSoundName(Emotion.SICK), "Sick emotion sound");
        assertEquals("snooty", MoeSounds.getEmotionSoundName(Emotion.SNOOTY), "Snooty emotion sound");
        assertEquals("smitten", MoeSounds.getEmotionSoundName(Emotion.SMITTEN), "Smitten emotion sound");
        assertEquals("yawn", MoeSounds.getEmotionSoundName(Emotion.TIRED), "Tired emotion sound");
    }

    private void testSpeakerKnownVoiceIdRoundTripsWithoutResolution() {
        Speaker speaker = new Speaker(speakerTag("NORMAL", "minecraft:entity.villager.ambient"));
        Speaker restored = new Speaker(speaker.write(new CompoundTag()));

        assertEquals(Emotion.NORMAL, restored.emotion, "Speaker emotion round trip");
        assertEquals(new ResourceLocation("minecraft:entity.villager.ambient"), restored.getVoiceID(), "Speaker voice ID round trip");
        assertNull(restored.voice, "Speaker does not resolve voice during pure parsing");
    }

    private void testSpeakerUnknownVoiceIdRoundTripsWithoutResolution() {
        Speaker speaker = new Speaker(speakerTag("HAPPY", "block_party:renamed_or_missing_voice"));
        Speaker restored = new Speaker(speaker.write(new CompoundTag()));

        assertEquals(Emotion.HAPPY, restored.emotion, "Speaker preserves emotion with unknown voice ID");
        assertEquals(new ResourceLocation("block_party:renamed_or_missing_voice"), restored.getVoiceID(), "Speaker preserves unknown-but-valid voice ID");
        assertNull(restored.voice, "Unknown speaker voice is not resolved during pure parsing");
    }

    private void testSpeakerInvalidVoiceIdFallsBackSafely() {
        Speaker speaker = new Speaker(speakerTag("NOT_REAL", "bad id"));

        assertEquals(Emotion.NORMAL, speaker.emotion, "Invalid speaker emotion falls back safely");
        assertNull(speaker.getVoiceID(), "Invalid speaker voice ID falls back to null");
    }

    private void testDialogueKnownSoundIdRoundTripsWithoutResolution() {
        Dialogue dialogue = new Dialogue(dialogueTag("minecraft:block.note_block.bell"));
        Dialogue restored = new Dialogue(dialogue.write());

        assertEquals(new ResourceLocation("minecraft:block.note_block.bell"), restored.getSoundID(), "Dialogue sound ID round trip");
        assertNull(restored.getSound(), "Dialogue does not resolve sound during pure parsing");
    }

    private void testDialogueRenamedOrMissingSoundIdPreservesSerializedId() {
        Dialogue dialogue = new Dialogue(dialogueTag("block_party:renamed_or_missing_dialogue_sound"));
        Dialogue restored = new Dialogue(dialogue.write());

        assertEquals(new ResourceLocation("block_party:renamed_or_missing_dialogue_sound"), restored.getSoundID(), "Dialogue preserves unknown-but-valid sound ID");
    }

    private void testDialogueMissingSoundFieldDoesNotCrashPureParsing() {
        CompoundTag tag = dialogueTag("minecraft:block.note_block.bell");
        tag.remove("Sound");
        Dialogue dialogue = new Dialogue(tag);

        assertNull(dialogue.getSoundID(), "Missing dialogue sound ID is null");
        assertEquals("", dialogue.write().getString("Sound"), "Missing dialogue sound serializes as empty ID");
    }

    private void testJsonUtilsKnownRegistryLookupReturnsNullKnownBug() {
        assertNull(JsonUtils.getAs(JsonUtils.SOUND_EVENT, "minecraft:block.note_block.bell"), "JsonUtils known sound registry lookup currently returns null");
    }

    private CompoundTag speakerTag(String emotion, String voice) {
        CompoundTag speaker = new CompoundTag();
        speaker.putString("Identity", Speaker.Identity.CHARACTER.name());
        speaker.putString("Position", Speaker.Position.LEFT.name());
        speaker.putString("Animation", "DEFAULT");
        speaker.putString("Emotion", emotion);
        speaker.putFloat("Scale", 1.0F);
        speaker.putBoolean("Speaks", true);
        speaker.putString("Voice", voice);
        return speaker;
    }

    private CompoundTag dialogueTag(String sound) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Text", "Registry contract");
        tag.putBoolean("Tooltip", false);
        tag.putString("Sound", sound);
        tag.put("Speaker", speakerTag("NORMAL", "minecraft:entity.villager.ambient"));
        return tag;
    }
}
