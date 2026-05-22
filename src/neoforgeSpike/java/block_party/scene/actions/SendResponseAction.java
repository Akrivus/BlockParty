package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.Response;
import block_party.scene.SceneAction;
import java.util.List;

public record SendResponseAction(Response icon, String text, List<SceneAction> actions) implements SceneAction {
    public SendResponseAction {
        actions = List.copyOf(actions);
    }

    @Override
    public void apply(Moe moe) {
        moe.sceneManager().putActions(this.actions);
    }
}
