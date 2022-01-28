package block_party.scene;

import block_party.entities.BlockPartyNPC;
import block_party.registry.CustomResources;
import block_party.registry.SceneActions;
import block_party.utils.Trans;
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

    public void tick() {
        this.setDefaultVariables(this.npc);
        if (this.action == null && this.actions.isEmpty()) { return; }
        if (this.action == null) {
            this.action = this.actions.remove();
            this.action.apply(this.npc);
        }
        if (this.action.isComplete(this.npc)) {
            this.action.onComplete(this.npc);
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
        if (this.action != null)
            this.action.onComplete(this.npc);
        this.action = action;
    }

    public void setActions(List<ISceneAction> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    public void putActions(List<ISceneAction> actions) {
        for (int i = actions.size(); i-- > 0;) { this.putAction(actions.get(i)); }
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

    private void setDefaultVariables(BlockPartyNPC actor) {
        this.cookies.add( "name",        actor.getGivenName());
        this.cookies.add( "family_name", actor.getFamilyName());
        this.cookies.add( "blood_type",  Trans.late(actor.getBloodType().getTranslationKey()));
        this.cookies.add( "dere",        Trans.late(actor.getDere().getTranslationKey()));
        this.cookies.add( "emotion",     Trans.late(actor.getEmotion().getTranslationKey()));
        this.cookies.add( "gender",      Trans.late(actor.getGender().getTranslationKey()));
        this.counters.set("health",      (int) actor.getHealth());
        this.counters.set("food_level",  (int) actor.getFoodLevel());
        this.counters.set("exhaustion",  (int) actor.getExhaustion());
        this.counters.set("saturation",  (int) actor.getSaturation());
        this.counters.set("stress",      (int) actor.getStress());
        this.counters.set("relaxation",  (int) actor.getRelaxation());
        this.counters.set("loyalty",     (int) actor.getLoyalty());
        this.counters.set("affection",   (int) actor.getAffection());
        this.counters.set("slouch",      (int) actor.getSlouch());
        this.counters.set("scale",       (int) actor.getScale());
        this.counters.set("age",         (int) actor.getAgeInYears());
    }
}
