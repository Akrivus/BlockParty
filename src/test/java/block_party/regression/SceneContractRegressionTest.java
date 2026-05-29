package block_party.regression;

import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.SceneTrigger;
import block_party.scene.Speaker;
import block_party.scene.actions.SendDialogue;
import block_party.scene.actions.SendResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertFalse;
import static block_party.regression.TestSupport.assertNull;
import static block_party.regression.TestSupport.getField;

final class SceneContractRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testSceneTriggerPriorities();
        testResponseParsingAndTranslationKeys();
        testDialogueNbtParsingWithMissingOptionalFields();
        testMalformedResponseBlocksFailSafely();
        testUnknownSpeakerIdsFallbackConsistently();
        testDialogueSoundIdsRemainStableResourceLocations();
        testResponseIdsRoundTripUnchanged();
        testDialogueResponsePageOrderingIsDeterministic();
        testEmptyDialoguePageAndResponsesDoNotCrash();
        testOldShapeResponseWithoutActionsDecodesAsEmptyActions();
        testSceneTransitionResponseIdsArePreserved();
        testSceneParsingDoesNotResolveSoundRegistries();
    }

    private void testSceneTriggerPriorities() {
        assertEquals(8, SceneTrigger.CREATION.getPriority(), "Creation trigger priority");
        assertEquals(8, SceneTrigger.HIDING_SPOT_DISCOVERED.getPriority(), "Hiding discovered trigger priority");
        assertEquals(8, SceneTrigger.ATTENTION.getPriority(), "Attention trigger priority");
        assertEquals(7, SceneTrigger.SHIFT_LEFT_CLICK.getPriority(), "Shift-left trigger priority");
        assertEquals(6, SceneTrigger.LEFT_CLICK.getPriority(), "Left-click trigger priority");
        assertEquals(6, SceneTrigger.SHIFT_RIGHT_CLICK.getPriority(), "Shift-right trigger priority");
        assertEquals(5, SceneTrigger.RIGHT_CLICK.getPriority(), "Right-click trigger priority");
        assertEquals(4, SceneTrigger.HURT.getPriority(), "Hurt trigger priority");
        assertEquals(3, SceneTrigger.ATTACK.getPriority(), "Attack trigger priority");
        assertEquals(2, SceneTrigger.STARE.getPriority(), "Stare trigger priority");
        assertEquals(1, SceneTrigger.EVERY_TICK.getPriority(), "Every-tick trigger priority");
        assertEquals(1, SceneTrigger.RANDOM_TICK.getPriority(), "Random-tick trigger priority");
        assertEquals(0, SceneTrigger.NULL.getPriority(), "Null trigger priority");
        assertEquals(SceneTrigger.RIGHT_CLICK, SceneTrigger.NULL.fromValue("right_click"), "SceneTrigger parses lowercase keys");
        assertEquals(SceneTrigger.LEFT_CLICK, SceneTrigger.LEFT_CLICK.fromValue("unknown"), "SceneTrigger falls back to receiver");
    }

    private void testResponseParsingAndTranslationKeys() {
        assertEquals(Response.GREEN_CHECKMARK, Response.CLOSE_DIALOGUE.fromValue("green_checkmark"), "Response parses lowercase keys");
        assertEquals(Response.CLOSE_DIALOGUE, Response.CLOSE_DIALOGUE.fromValue("missing"), "Response falls back to receiver");
        assertEquals("gui.block_party.dialogue.response.lovely_heart", Response.LOVELY_HEART.getTranslationKey(), "Response translation key");
    }

    private void testDialogueNbtParsingWithMissingOptionalFields() {
        Dialogue dialogue = new Dialogue(dialogueTag("Hello", null, new ListTag()));

        assertEquals("Hello", dialogue.getText(), "Dialogue missing optional fields preserves text");
        assertFalse(dialogue.isTooltip(), "Dialogue missing tooltip defaults false");
        assertNull(dialogue.getSoundID(), "Dialogue missing sound ID defaults null");
        assertEquals(Speaker.Identity.CHARACTER, dialogue.getSpeaker().identity, "Dialogue missing speaker identity defaults character");
        assertEquals(0, dialogue.getResponses().size(), "Dialogue missing responses default empty");
    }

    private void testMalformedResponseBlocksFailSafely() {
        SendDialogue page = new SendDialogue();
        page.parse(jsonObject("{\"text\":\"Hi\",\"speaker\":{},\"responses\":[\"bad\",{}, {\"icon\":\"bad id\",\"actions\":\"bad\"}, {\"icon\":\"block_party:next_response\",\"actions\":[\"bad id\"]}]}"));

        assertEquals(2, page.responses.size(), "Malformed response entries are skipped or folded into safe defaults");
        assertEquals(Response.CLOSE_DIALOGUE, page.responses.keySet().iterator().next(), "Invalid response icon falls back to close");
        assertEquals(Response.NEXT_RESPONSE, page.responses.keySet().toArray()[1], "Valid response after malformed action keeps icon");
        page.responses.values().forEach((response) -> assertEquals(0, ((java.util.List) getField(response, "actions")).size(), "Malformed response actions become empty"));
    }

    private void testUnknownSpeakerIdsFallbackConsistently() {
        Speaker speaker = new Speaker(speakerTag("stranger", "sideways", "not_an_animation", "not_an_emotion"));

        assertEquals(Speaker.Identity.CHARACTER, speaker.identity, "Unknown speaker identity falls back to character");
        assertEquals(Speaker.Position.LEFT, speaker.position, "Unknown speaker position falls back to left");
        assertEquals(block_party.client.animation.Animation.DEFAULT, speaker.animation, "Unknown speaker animation falls back to default");
        assertEquals(block_party.scene.traits.Emotion.NORMAL, speaker.emotion, "Unknown speaker emotion falls back to normal");
    }

    private void testDialogueSoundIdsRemainStableResourceLocations() {
        Dialogue dialogue = new Dialogue(dialogueTag("Sound", "block_party:dialogue.custom", new ListTag()));
        Dialogue restored = new Dialogue(dialogue.write());

        assertEquals(new ResourceLocation("block_party:dialogue.custom"), restored.getSoundID(), "Dialogue sound ID round trips unchanged");
        assertNull(restored.getSound(), "Dialogue sound is not resolved during ID round trip");
    }

    private void testResponseIdsRoundTripUnchanged() {
        ListTag responses = new ListTag();
        responses.add(responseTag("LOVELY_HEART", "Love"));
        responses.add(responseTag("OPEN_DIALOGUE", "Open"));
        Dialogue restored = new Dialogue(new Dialogue(dialogueTag("Responses", "minecraft:block.note_block.bell", responses)).write());

        assertEquals("Love", restored.getResponses().get(Response.LOVELY_HEART), "Lovely heart response ID round trips");
        assertEquals("Open", restored.getResponses().get(Response.OPEN_DIALOGUE), "Open dialogue response ID round trips");
    }

    private void testDialogueResponsePageOrderingIsDeterministic() {
        Dialogue dialogue = new Dialogue(dialogueTag("Ordered", "minecraft:block.note_block.bell", new ListTag()));
        dialogue.add(Response.GREEN_CHECKMARK, "first");
        dialogue.add(Response.RED_X, "second");
        dialogue.add(Response.NEXT_RESPONSE, "third");

        ListTag written = dialogue.write().getList("Responses", block_party.utils.NBT.COMPOUND);
        assertEquals("GREEN_CHECKMARK", written.getCompound(0).getString("Icon"), "First response stays first");
        assertEquals("RED_X", written.getCompound(1).getString("Icon"), "Second response stays second");
        assertEquals("NEXT_RESPONSE", written.getCompound(2).getString("Icon"), "Third response stays third");
    }

    private void testEmptyDialoguePageAndResponsesDoNotCrash() {
        SendDialogue page = new SendDialogue();
        page.parse(new JsonObject());
        SendResponse response = new SendResponse();
        response.parse(new JsonObject());

        assertEquals("", getField(page, "text"), "Empty dialogue page defaults text");
        assertEquals(0, page.responses.size(), "Empty dialogue page defaults responses");
        assertEquals(Response.CLOSE_DIALOGUE, response.getIcon(), "Empty response defaults close icon");
        assertEquals(0, ((java.util.List) getField(response, "actions")).size(), "Empty response defaults actions");
    }

    private void testOldShapeResponseWithoutActionsDecodesAsEmptyActions() {
        SendResponse response = new SendResponse();
        response.parse(jsonObject("{\"icon\":\"block_party:next_response\",\"text\":\"Continue\"}"));

        assertEquals(Response.NEXT_RESPONSE, response.getIcon(), "Old response shape preserves icon");
        assertEquals("Continue", response.getText(), "Old response shape preserves text");
        assertEquals(0, ((java.util.List) getField(response, "actions")).size(), "Old response shape has empty actions");
    }

    private void testSceneTransitionResponseIdsArePreserved() {
        SendResponse response = new SendResponse();
        response.parse(jsonObject("{\"icon\":\"block_party:open_dialogue\",\"text\":\"Open target\",\"actions\":[]}"));

        assertEquals(Response.OPEN_DIALOGUE, response.getIcon(), "Scene transition response ID is preserved");
        assertEquals("Open target", response.getText(), "Scene transition response text is preserved");
    }

    private void testSceneParsingDoesNotResolveSoundRegistries() {
        SendDialogue page = new SendDialogue();
        page.parse(jsonObject("{\"text\":\"Hi\",\"speaker\":{\"voice\":\"block_party:voice.future\",\"emotion\":\"happy\"},\"responses\":[]}"));
        Speaker speaker = page.getSpeaker();

        assertEquals(new ResourceLocation("block_party:voice.future"), speaker.getVoiceID(), "Scene speaker stores voice ID");
        assertNull(getField(speaker, "voice"), "Scene speaker parse does not resolve SoundEvent");
    }

    private CompoundTag dialogueTag(String text, String sound, ListTag responses) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Text", text);
        tag.put("Speaker", new Speaker(new JsonObject()).write(new CompoundTag()));
        tag.put("Responses", responses);
        if (sound != null) {
            tag.putString("Sound", sound);
        }
        return tag;
    }

    private CompoundTag responseTag(String icon, String text) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Icon", icon);
        tag.putString("Text", text);
        return tag;
    }

    private JsonObject speakerTag(String identity, String position, String animation, String emotion) {
        JsonObject speaker = new JsonObject();
        speaker.addProperty("identity", identity);
        speaker.addProperty("position", position);
        speaker.addProperty("animation", animation);
        speaker.addProperty("emotion", emotion);
        return speaker;
    }

    private JsonObject jsonObject(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
