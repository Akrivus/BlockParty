package block_party.scene;

import block_party.npc.BlockPartyNPC;
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

    public void tick(BlockPartyNPC npc) {
        this.setDefaultVariables(npc);
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

    private void setDefaultVariables(BlockPartyNPC npc) {
        this.cookies.add( "given_name",  npc.getGivenName());
        this.cookies.add( "family_name", npc.getBlockName());
        this.cookies.add( "blood_type",  Trans.late(npc.getBloodType().getTranslationKey()));
        this.cookies.add( "dere",        Trans.late(npc.getDere().getTranslationKey()));
        this.cookies.add( "emotion",     Trans.late(npc.getEmotion().getTranslationKey()));
        this.cookies.add( "gender",      Trans.late(npc.getGender().getTranslationKey()));
        this.counters.set("health",      (int) npc.getHealth());
        this.counters.set("food_level",  (int) npc.getFoodLevel());
        this.counters.set("exhaustion",  (int) npc.getExhaustion());
        this.counters.set("saturation",  (int) npc.getSaturation());
        this.counters.set("stress",      (int) npc.getStress());
        this.counters.set("relaxation",  (int) npc.getRelaxation());
        this.counters.set("loyalty",     (int) npc.getLoyalty());
        this.counters.set("affection",   (int) npc.getAffection());
        this.counters.set("slouch",      (int) npc.getSlouch());
        this.counters.set("scale",       (int) npc.getScale());
        this.counters.set("age",         (int) npc.getAgeInYears());
    }
}
