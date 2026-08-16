package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TimedSceneAction implements SceneAction {
    private final Kind kind;
    private final String value;
    private final int minimumTicks;
    private final int maximumTicks;
    private final Map<UUID, Long> ends = new ConcurrentHashMap<>();
    private final Map<UUID, String> previous = new ConcurrentHashMap<>();

    public TimedSceneAction(Kind kind, String value, int minimumTicks, int maximumTicks) {
        this.kind = kind == null ? Kind.WAIT : kind;
        this.value = value == null ? "" : value;
        this.minimumTicks = Math.max(0, minimumTicks);
        this.maximumTicks = Math.max(this.minimumTicks, maximumTicks);
    }

    @Override
    public void apply(Moe moe) {
        int duration = this.maximumTicks <= this.minimumTicks ? this.minimumTicks
                : this.minimumTicks + moe.getRandom().nextInt(this.maximumTicks - this.minimumTicks + 1);
        this.ends.put(moe.getUUID(), moe.level().getGameTime() + duration);
        if (this.kind == Kind.ANIMATION) {
            moe.setTemporaryAnimationKey(this.value, duration);
        } else if (this.kind == Kind.EMOTION) {
            this.previous.put(moe.getUUID(), moe.getEmotion());
            moe.setEmotion(this.value);
        }
    }

    @Override
    public boolean isComplete(Moe moe) {
        return moe.level().getGameTime() >= this.ends.getOrDefault(moe.getUUID(), moe.level().getGameTime());
    }

    @Override
    public void onComplete(Moe moe) {
        this.ends.remove(moe.getUUID());
        if (this.kind == Kind.EMOTION) {
            moe.setEmotion(this.previous.remove(moe.getUUID()));
        }
    }

    public enum Kind { WAIT, ANIMATION, EMOTION }
}
