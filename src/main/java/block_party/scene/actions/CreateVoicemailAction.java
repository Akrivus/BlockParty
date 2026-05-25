package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.scene.Speaker;
import block_party.db.voicemail.Voicemails;
import net.minecraft.resources.ResourceLocation;

public record CreateVoicemailAction(String text, boolean tooltip, Speaker speaker, ResourceLocation sound, long delayMillis) implements SceneAction {
    private static final long DEFAULT_DELAY_MILLIS = 60L * 60L * 1000L;

    public CreateVoicemailAction(String text, boolean tooltip, Speaker speaker, ResourceLocation sound) {
        this(text, tooltip, speaker, sound, DEFAULT_DELAY_MILLIS);
    }

    @Override
    public void apply(Moe moe) {
        Voicemails.get(moe.level()).add(moe.getOwnerUUID(), moe.getDatabaseID(), this.text, this.tooltip, this.speaker, this.sound, this.delayMillis);
    }
}
