package block_party.scene;

import block_party.entities.Moe;
import block_party.registry.CustomResources;
import block_party.scene.actions.EndAction;
import java.util.LinkedList;
import java.util.List;

public final class SceneManager {
    private final Moe moe;
    private final LinkedList<SceneAction> actions = new LinkedList<>();
    private SceneTrigger trigger = SceneTrigger.CREATION;
    private SceneAction action = EndAction.INSTANCE;

    public SceneManager(Moe moe) {
        this.moe = moe;
    }

    public void tick() {
        if (this.action == null && this.actions.isEmpty()) {
            return;
        }
        if (this.action == null) {
            this.action = this.actions.remove();
            this.action.apply(this.moe);
        }
        if (this.action.isComplete(this.moe)) {
            this.action.onComplete(this.moe);
            this.action = null;
        }
        if (this.action == null) {
            this.trigger = SceneTrigger.NULL;
        }
    }

    public boolean trigger(SceneTrigger trigger) {
        if (this.trigger.getPriority() >= trigger.getPriority()) {
            return false;
        }
        Scene scene = CustomResources.SCENES.get(trigger, this.moe);
        if (scene == null) {
            return false;
        }
        this.trigger = trigger;
        this.setAction(null);
        this.setActions(scene.getActions());
        return true;
    }

    public void setAction(SceneAction action) {
        if (this.action != null) {
            this.action.onComplete(this.moe);
        }
        this.action = action;
    }

    public void setActions(List<SceneAction> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    public void putActions(List<SceneAction> actions) {
        for (int index = actions.size(); index-- > 0;) {
            this.putAction(actions.get(index));
        }
    }

    public void putAction(SceneAction action) {
        this.actions.addFirst(action);
    }

    public SceneTrigger getTriggerForTests() {
        return this.trigger;
    }
}
