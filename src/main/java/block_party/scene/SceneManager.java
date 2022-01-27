package block_party.scene;

import block_party.npc.BlockPartyNPC;
import block_party.registry.CustomResources;
import block_party.registry.SceneActions;
import block_party.scene.*;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedList;
import java.util.List;

public class SceneManager {
    protected final BlockPartyNPC npc;
    protected SceneTrigger trigger = SceneTrigger.CREATION;
    protected LinkedList<ISceneAction> actions;
    protected ISceneAction action;
    public Cookies cookies = new Cookies();
    public Counters counters = new Counters();

    public SceneManager(BlockPartyNPC npc) {
        this.actions = Lists.newLinkedList();
        this.action = SceneActions.build(SceneActions.END);
        this.npc = npc;
    }

    public void read(CompoundTag compound) {
        this.cookies = new Cookies(compound);
        this.counters = new Counters(compound);
    }

    public void write(CompoundTag compound) {
        this.cookies.save(compound);
        this.counters.save(compound);
    }

    public void tick(BlockPartyNPC npc) {
        if (this.action == null && this.actions.isEmpty()) { return; }
        if (this.action == null) {
            this.action = this.actions.remove();
            this.action.apply(npc);
        }
        if (this.action.isComplete()) {
            this.action.onComplete();
            this.action = null;
        }
        if (this.action == null) { this.trigger = SceneTrigger.NULL; }
    }

    public void trigger(SceneTrigger trigger) {
        if (this.trigger.getPriority() < trigger.getPriority()) {
            this.trigger = trigger;
            Scene scene = CustomResources.SCENES.get(trigger, this.npc);
            if (scene == null) { return; }
            this.setAction(null);
            this.setActions(scene.getActions());
        }
    }

    public void setAction(ISceneAction action) {
        if (this.action != null) { this.action.onComplete(); }
        this.action = action;
    }

    public void setActions(List<ISceneAction> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    public void putActions(List<ISceneAction> actions) {
        for (int i = actions.size() - 1; i > 0; --i) { this.putAction(actions.get(i)); }
    }

    public void putAction(ISceneAction action) {
        this.actions.addFirst(action);
    }

    public void addActions(List<ISceneAction> actions) {
        for (int i = 0; i < actions.size(); ++i) { this.addAction(actions.get(i)); }
    }

    public void addAction(ISceneAction action) {
        this.actions.addLast(action);
    }
}
