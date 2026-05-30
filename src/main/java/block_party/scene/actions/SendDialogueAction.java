package block_party.scene.actions;

import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.entities.social.MoeSocialContext;
import block_party.network.payload.DialogueOpenPayload;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.SceneAction;
import block_party.scene.Speaker;
import block_party.utils.Markdown;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SendDialogueAction implements SceneAction {
    private final String text;
    private final boolean tooltip;
    private final Speaker speaker;
    private final ResourceLocation sound;
    private final Map<Response, SendResponseAction> responses;

    public SendDialogueAction(String text, boolean tooltip, Speaker speaker, ResourceLocation sound, Map<Response, SendResponseAction> responses) {
        this.text = text;
        this.tooltip = tooltip;
        this.speaker = speaker;
        this.sound = sound;
        this.responses = Map.copyOf(new LinkedHashMap<>(responses));
    }

    @Override
    public void apply(Moe moe) {
        Dialogue dialogue = new Dialogue(this.resolveText(moe), this.tooltip, this.speaker, this.sound, this.responseText());
        moe.setDialogue(dialogue);
        this.sendToTargetPlayer(moe, dialogue);
    }

    @Override
    public boolean isComplete(Moe moe) {
        return moe.hasResponse();
    }

    @Override
    public void onComplete(Moe moe) {
        SendResponseAction action = this.responses.get(moe.getResponse());
        moe.sceneManager().putAction(action == null ? EndAction.INSTANCE : action);
    }

    private Map<Response, String> responseText() {
        Map<Response, String> texts = new LinkedHashMap<>();
        this.responses.forEach((icon, response) -> texts.put(icon, Markdown.mark(response.text())));
        return texts;
    }

    private void sendToTargetPlayer(Moe moe, Dialogue dialogue) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return;
        }
        ServerPlayer player = level.getServer().getPlayerList().getPlayer(moe.getDialogueTarget());
        if (player != null) {
            PacketDistributor.sendToPlayer(player, DialogueOpenPayload.response(BlockPartyDB.get(level), player.getUUID(), moe.getDatabaseID(), dialogue));
        }
    }

    private String resolveText(Moe moe) {
        String ownerName = "";
        if (moe.level() instanceof ServerLevel level) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(moe.getDialogueTarget());
            if (player != null) {
                ownerName = player.getName().getString();
            }
        }
        String resolved = this.text
                .replace("@.name", ownerName)
                .replace("@social.name", socialTargetName(moe))
                .replace("@nearby.names", nearbyNames(moe))
                .replace("@nearby.name", nearbyName(moe))
                .replace("@family_name", moe.getFamilyName())
                .replace("@name", moe.getGivenName());
        return Markdown.markWithSubs(resolved, moe);
    }

    private static String socialTargetName(Moe moe) {
        return MoeSocialContext.find(moe, 8.0D)
                .map(context -> context.target().getGivenName())
                .orElse("");
    }

    private static String nearbyName(Moe moe) {
        List<Moe> nearby = MoeSocialContext.nearby(moe, 8.0D);
        return nearby.isEmpty() ? "" : nearby.getFirst().getGivenName();
    }

    private static String nearbyNames(Moe moe) {
        return MoeSocialContext.nearby(moe, 8.0D).stream()
                .limit(3)
                .map(Moe::getGivenName)
                .collect(Collectors.joining(", "));
    }
}
