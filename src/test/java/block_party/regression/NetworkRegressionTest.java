package block_party.regression;

import block_party.db.records.NPC;
import block_party.messages.CDialogueClose;
import block_party.messages.CDialogueRespond;
import block_party.messages.CNPCRemove;
import block_party.messages.CNPCRequest;
import block_party.messages.CNPCTeleport;
import block_party.messages.CRemovePage;
import block_party.messages.SCloseDialogue;
import block_party.messages.SNPCList;
import block_party.messages.SNPCResponse;
import block_party.messages.SOpenCellPhone;
import block_party.messages.SOpenDialogue;
import block_party.messages.SOpenYearbook;
import block_party.messages.SShrineList;
import block_party.scene.Response;
import block_party.scene.Speaker;
import block_party.scene.Dialogue;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertNull;
import static block_party.regression.TestSupport.assertThrows;
import static block_party.regression.TestSupport.getField;
import static block_party.regression.TestSupport.setField;

final class NetworkRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testCDialogueRespondRoundTrip();
        testCDialogueCloseRoundTripUsesCloseDialogueResponse();
        testCNPCQueryDerivedMessagesRoundTripOnlyNpcId();
        testSCloseDialogueEmptyPayloadRoundTrip();
        testSNPCListRoundTrip();
        testSNPCListEmptyPayloadRoundTrip();
        testSOpenYearbookRoundTrip();
        testSOpenCellPhoneRoundTripDefaultsSelectedIdToMinusOne();
        testSShrineListRoundTrip();
        testSShrineListEmptyPayloadRoundTrip();
        testSNPCResponseRoundTrip();
        testSNPCResponseNullPayloadDecodeKnownBug();
        testSOpenDialogueRoundTripPreservesRegistryIdsWithoutResolving();
        testBackwardsCompatibleDialogueCloseDecodeFromDialogueRespondPayload();
        testInvalidResponseOrdinalFailsDuringDecode();
        testTruncatedNpcQueryPayloadFailsDuringDecode();
    }

    private void testCDialogueRespondRoundTrip() {
        CDialogueRespond decoded = decode(encode(new CDialogueRespond(42L, Response.LOVELY_HEART)), CDialogueRespond::new);

        assertEquals(42L, getField(decoded, "id"), "CDialogueRespond preserves NPC ID");
        assertEquals(Response.LOVELY_HEART, getField(decoded, "response"), "CDialogueRespond preserves response");
    }

    private void testCDialogueCloseRoundTripUsesCloseDialogueResponse() {
        CDialogueClose decoded = decode(encode(new CDialogueClose(43L)), CDialogueClose::new);

        assertEquals(43L, getField(decoded, "id"), "CDialogueClose preserves NPC ID");
        assertEquals(Response.CLOSE_DIALOGUE, getField(decoded, "response"), "CDialogueClose response is close dialogue");
    }

    private void testCNPCQueryDerivedMessagesRoundTripOnlyNpcId() {
        assertEquals(100L, getField(decode(encode(new CNPCRemove(100L)), CNPCRemove::new), "id"), "CNPCRemove preserves NPC ID");
        assertEquals(101L, getField(decode(encode(new CNPCRequest(101L)), CNPCRequest::new), "id"), "CNPCRequest preserves NPC ID");
        assertEquals(102L, getField(decode(encode(new CNPCTeleport(102L)), CNPCTeleport::new), "id"), "CNPCTeleport preserves NPC ID");
        assertEquals(103L, getField(decode(encode(new CRemovePage(103L)), CRemovePage::new), "id"), "CRemovePage preserves NPC ID");
    }

    private void testSCloseDialogueEmptyPayloadRoundTrip() {
        FriendlyByteBuf buffer = encode(new SCloseDialogue());
        assertEquals(0, buffer.readableBytes(), "SCloseDialogue encodes no payload");
        decode(buffer, SCloseDialogue::new);
    }

    private void testSNPCListRoundTrip() {
        SNPCList decoded = decode(encode(new SNPCList(Arrays.asList(1L, 7L, 13L))), SNPCList::new);

        assertEquals(Arrays.asList(1L, 7L, 13L), getField(decoded, "npcs"), "SNPCList preserves NPC IDs in order");
    }

    private void testSNPCListEmptyPayloadRoundTrip() {
        SNPCList decoded = decode(encode(new SNPCList(Collections.emptyList())), SNPCList::new);

        assertEquals(Collections.emptyList(), getField(decoded, "npcs"), "SNPCList preserves empty NPC list");
    }

    private void testSOpenYearbookRoundTrip() {
        SOpenYearbook decoded = decode(encode(new SOpenYearbook(Arrays.asList(2L, 3L), 3L, InteractionHand.OFF_HAND)), SOpenYearbook::new);

        assertEquals(Arrays.asList(2L, 3L), getField(decoded, "npcs"), "SOpenYearbook preserves NPC list");
        assertEquals(3L, getField(decoded, "id"), "SOpenYearbook preserves selected NPC ID");
        assertEquals(InteractionHand.OFF_HAND, getField(decoded, "hand"), "SOpenYearbook preserves hand");
    }

    private void testSOpenCellPhoneRoundTripDefaultsSelectedIdToMinusOne() {
        SOpenCellPhone decoded = decode(encode(new SOpenCellPhone(Collections.emptyList(), InteractionHand.MAIN_HAND)), SOpenCellPhone::new);

        assertEquals(Collections.emptyList(), getField(decoded, "npcs"), "SOpenCellPhone preserves empty NPC list");
        assertEquals(-1L, getField(decoded, "id"), "SOpenCellPhone selected ID defaults to -1");
        assertEquals(InteractionHand.MAIN_HAND, getField(decoded, "hand"), "SOpenCellPhone preserves hand");
    }

    private void testSShrineListRoundTrip() {
        SShrineList message = decode(shrineBuffer(Arrays.asList(new BlockPos(1, 2, 3), new BlockPos(-4, 70, 9))), SShrineList::new);
        FriendlyByteBuf encoded = encode(message);
        SShrineList decoded = decode(encoded, SShrineList::new);

        assertEquals(Arrays.asList(new BlockPos(1, 2, 3), new BlockPos(-4, 70, 9)), getField(decoded, "shrines"), "SShrineList preserves shrine positions");
    }

    private void testSShrineListEmptyPayloadRoundTrip() {
        SShrineList decoded = decode(encode(decode(shrineBuffer(Collections.emptyList()), SShrineList::new)), SShrineList::new);

        assertEquals(Collections.emptyList(), getField(decoded, "shrines"), "SShrineList preserves empty shrine list");
    }

    private void testSNPCResponseRoundTrip() {
        SNPCResponse decoded = decode(encode(new SNPCResponse(new NPC(npcTag(77L, "Aki")))), SNPCResponse::new);
        NPC npc = (NPC) getField(decoded, "npc");

        assertEquals(77L, npc.getID(), "SNPCResponse preserves NPC ID");
        assertEquals("Aki", npc.getName(), "SNPCResponse preserves NPC name");
    }

    private void testSNPCResponseNullPayloadDecodeKnownBug() {
        FriendlyByteBuf buffer = newBuffer();
        buffer.writeNbt(null);

        assertThrows(NullPointerException.class, () -> new SNPCResponse(buffer), "SNPCResponse null NBT payload currently fails during decode");
    }

    private void testSOpenDialogueRoundTripPreservesRegistryIdsWithoutResolving() {
        FriendlyByteBuf buffer = newBuffer();
        buffer.writeNbt(npcTag(88L, "Mika"));
        buffer.writeNbt(dialogueTag());
        SOpenDialogue decoded = new SOpenDialogue(buffer);
        NPC npc = (NPC) getField(decoded, "npc");
        Dialogue dialogue = (Dialogue) getField(decoded, "dialogue");

        assertEquals(88L, npc.getID(), "SOpenDialogue preserves NPC ID");
        assertEquals("Mika", npc.getName(), "SOpenDialogue preserves NPC name");
        assertEquals("Hello there", dialogue.getText(), "SOpenDialogue preserves dialogue text");
        assertEquals(new ResourceLocation("minecraft:block.note_block.bell"), dialogue.getSoundID(), "SOpenDialogue preserves sound registry ID");
        assertEquals(Speaker.Identity.NARRATOR, dialogue.getSpeaker().identity, "SOpenDialogue preserves speaker identity");
        assertEquals(new ResourceLocation("minecraft:entity.villager.ambient"), dialogue.getSpeaker().getVoiceID(), "SOpenDialogue preserves speaker voice registry ID");
        assertNull(dialogue.getSound(), "SOpenDialogue does not resolve sound during pure decode");
    }

    private void testBackwardsCompatibleDialogueCloseDecodeFromDialogueRespondPayload() {
        CDialogueClose decoded = decode(encode(new CDialogueRespond(44L, Response.CLOSE_DIALOGUE)), CDialogueClose::new);

        assertEquals(44L, getField(decoded, "id"), "CDialogueClose decodes old CDialogueRespond-shaped close payload ID");
        assertEquals(Response.CLOSE_DIALOGUE, getField(decoded, "response"), "CDialogueClose decodes old CDialogueRespond-shaped close payload response");
    }

    private void testInvalidResponseOrdinalFailsDuringDecode() {
        FriendlyByteBuf buffer = newBuffer();
        buffer.writeLong(45L);
        buffer.writeVarInt(999);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> new CDialogueRespond(buffer), "CDialogueRespond invalid response ordinal currently fails during decode");
    }

    private void testTruncatedNpcQueryPayloadFailsDuringDecode() {
        assertThrows(IndexOutOfBoundsException.class, () -> new CNPCRequest(newBuffer()), "CNPCRequest truncated payload currently fails during decode");
    }

    private CompoundTag npcTag(long id, String name) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DatabaseID", id);
        tag.put("PosDim", dimPosTag("minecraft:overworld", 1, 2, 3, false));
        tag.putUUID("PlayerUUID", new UUID(0L, 0L));
        tag.putBoolean("Dead", false);
        tag.putString("Name", name);
        tag.putString("BloodType", "O");
        tag.putString("Dere", "NYANDERE");
        tag.putFloat("Health", 20.0F);
        tag.putFloat("FoodLevel", 20.0F);
        tag.putFloat("Exhaustion", 0.0F);
        tag.putFloat("Saturation", 5.0F);
        tag.putFloat("Stress", 0.0F);
        tag.putFloat("Relaxation", 0.0F);
        tag.putFloat("Loyalty", 0.0F);
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

    private CompoundTag dialogueTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Text", "Hello there");
        tag.putBoolean("Tooltip", true);
        tag.putString("Sound", "minecraft:block.note_block.bell");
        CompoundTag speaker = new CompoundTag();
        speaker.putString("Identity", Speaker.Identity.NARRATOR.name());
        speaker.putString("Position", Speaker.Position.LEFT.name());
        speaker.putString("Animation", "DEFAULT");
        speaker.putString("Emotion", "NORMAL");
        speaker.putFloat("Scale", 1.0F);
        speaker.putBoolean("Speaks", true);
        speaker.putString("Voice", "minecraft:entity.villager.ambient");
        tag.put("Speaker", speaker);
        return tag;
    }

    private CompoundTag dimPosTag(String dim, int x, int y, int z, boolean empty) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Coordinates", new BlockPos(x, y, z).asLong());
        tag.putString("Dimension", dim);
        tag.putBoolean("IsEmpty", empty);
        return tag;
    }

    private FriendlyByteBuf shrineBuffer(List<BlockPos> positions) {
        FriendlyByteBuf buffer = newBuffer();
        buffer.writeInt(positions.size());
        for (BlockPos pos : positions) {
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        }
        return buffer;
    }

    private FriendlyByteBuf encode(block_party.messages.AbstractMessage message) {
        FriendlyByteBuf buffer = newBuffer();
        message.encode(buffer);
        return buffer;
    }

    private <T> T decode(FriendlyByteBuf buffer, Decoder<T> decoder) {
        return decoder.decode(buffer);
    }

    private FriendlyByteBuf newBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    private interface Decoder<T> {
        T decode(FriendlyByteBuf buffer);
    }
}
