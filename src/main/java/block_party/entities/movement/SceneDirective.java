package block_party.entities.movement;

import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.scene.SceneTrigger;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class SceneDirective {
    private Kind kind = Kind.NONE;
    private Status status = Status.NONE;
    private DimBlockPos location = new DimBlockPos();
    private UUID target = new UUID(0L, 0L);
    private String name = "";
    private String scope = "npc";
    private String selector = "";
    private String id = "";
    private DimBlockPos blockTarget = new DimBlockPos();
    private String resultId = "";
    private Status resultStatus = Status.NONE;
    private String failureReason = "";
    private double speed = 1.0D;
    private double arrivalRadius = 2.0D;
    private int ticksRemaining;
    private boolean terminalTriggerSent;

    public void assignLocation(String id, DimBlockPos location, String name, String scope, double speed, double radius, int timeout) {
        this.preserveReplacedResult();
        this.kind = Kind.LOCATION;
        this.status = Status.ACTIVE;
        this.location = location == null ? new DimBlockPos() : location;
        this.target = new UUID(0L, 0L);
        this.name = name == null ? "" : name;
        this.scope = scope == null ? "npc" : scope;
        this.selector = "";
        this.id = id == null ? "" : id;
        this.blockTarget = new DimBlockPos();
        this.configure(speed, radius, timeout);
    }

    public void assignEntity(String id, UUID target, String selector, double speed, double radius, int timeout) {
        this.preserveReplacedResult();
        this.kind = Kind.ENTITY;
        this.status = Status.ACTIVE;
        this.location = new DimBlockPos();
        this.target = target == null ? new UUID(0L, 0L) : target;
        this.name = "";
        this.scope = "npc";
        this.selector = selector == null ? "" : selector;
        this.id = id == null ? "" : id;
        this.blockTarget = new DimBlockPos();
        this.configure(speed, radius, timeout);
    }

    public void assignBlock(String id, DimBlockPos standingPosition, DimBlockPos blockTarget, String selector,
            double speed, double radius, int timeout) {
        this.preserveReplacedResult();
        this.kind = Kind.BLOCK;
        this.status = Status.ACTIVE;
        this.location = standingPosition == null ? new DimBlockPos() : standingPosition;
        this.blockTarget = blockTarget == null ? new DimBlockPos() : blockTarget;
        this.target = new UUID(0L, 0L);
        this.name = "";
        this.scope = "npc";
        this.selector = selector == null ? "" : selector;
        this.id = id == null ? "" : id;
        this.configure(speed, radius, timeout);
    }

    private void preserveReplacedResult() {
        if (this.status == Status.ACTIVE) {
            this.resultId = this.id;
            this.resultStatus = Status.CANCELLED;
            this.failureReason = "cancelled";
        }
    }

    private void configure(double speed, double radius, int timeout) {
        this.speed = Math.max(0.05D, speed);
        this.arrivalRadius = Math.max(0.25D, radius);
        this.ticksRemaining = Math.max(0, timeout);
        this.terminalTriggerSent = false;
    }

    public void cancel(Moe moe) {
        if (this.status == Status.ACTIVE) {
            this.finish(moe, Status.CANCELLED, "cancelled");
        } else {
            this.kind = Kind.NONE;
            this.status = Status.NONE;
        }
    }

    public void tick(Moe moe) {
        if (this.status != Status.ACTIVE) {
            this.dispatchTerminalTrigger(moe);
            return;
        }
        if (moe.shouldSkipGoalMovement() || moe.isFollowing()) {
            return;
        }
        Vec3 destination = this.destination(moe);
        if (destination == null) {
            this.finish(moe, Status.UNREACHABLE, this.unreachableReason(moe));
            this.dispatchTerminalTrigger(moe);
            return;
        }
        if (moe.position().distanceToSqr(destination) <= this.arrivalRadius * this.arrivalRadius) {
            this.finish(moe, Status.ARRIVED, "");
            this.dispatchTerminalTrigger(moe);
            return;
        }
        if (this.ticksRemaining > 0 && --this.ticksRemaining == 0) {
            this.finish(moe, Status.TIMED_OUT, "timed_out");
            this.dispatchTerminalTrigger(moe);
        }
    }

    public boolean canMove(Moe moe) {
        return this.status == Status.ACTIVE && !moe.shouldSkipGoalMovement() && !moe.isFollowing()
                && this.destination(moe) != null;
    }

    public boolean updateMovement(Moe moe) {
        Vec3 destination = this.destination(moe);
        if (destination == null || this.status != Status.ACTIVE) {
            return false;
        }
        moe.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, this.speed);
        return true;
    }

    public Vec3 destination(Moe moe) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return null;
        }
        if (this.kind == Kind.LOCATION || this.kind == Kind.BLOCK) {
            return !this.location.isEmpty() && this.location.getDim().equals(level.dimension())
                    ? Vec3.atBottomCenterOf(this.location.getPos()) : null;
        }
        if (this.kind == Kind.ENTITY) {
            Entity entity = level.getEntity(this.target);
            return entity != null && entity.isAlive() && !entity.isRemoved() ? entity.position() : null;
        }
        return null;
    }

    private String unreachableReason(Moe moe) {
        if ((this.kind == Kind.LOCATION || this.kind == Kind.BLOCK) && this.location.isEmpty()) {
            return this.kind == Kind.BLOCK ? "missing_block" : "missing_location";
        }
        if ((this.kind == Kind.LOCATION || this.kind == Kind.BLOCK)
                && !this.location.getDim().equals(moe.level().dimension())) return "wrong_dimension";
        return "missing_target";
    }

    private void finish(Moe moe, Status status, String reason) {
        this.status = status;
        this.resultId = this.id;
        this.resultStatus = status;
        this.failureReason = reason == null ? "" : reason;
        moe.getNavigation().stop();
    }

    private void dispatchTerminalTrigger(Moe moe) {
        if (this.status == Status.ACTIVE || this.status == Status.NONE) {
            return;
        }
        if (!this.terminalTriggerSent) {
            this.terminalTriggerSent = true;
            SceneTrigger trigger = switch (this.status) {
                case ARRIVED -> SceneTrigger.ASSIGNMENT_ARRIVED;
                case CANCELLED -> SceneTrigger.ASSIGNMENT_CANCELLED;
                case UNREACHABLE, TIMED_OUT -> SceneTrigger.ASSIGNMENT_FAILED;
                default -> SceneTrigger.NULL;
            };
            if (trigger != SceneTrigger.NULL) moe.triggerScene(trigger);
        }
    }

    public boolean assigned() { return this.kind != Kind.NONE; }
    public Kind kind() { return this.kind; }
    public Status status() { return this.status; }
    public String name() { return this.name; }
    public String scope() { return this.scope; }
    public String selector() { return this.selector; }
    public String id() { return this.id; }
    public DimBlockPos blockTarget() { return this.blockTarget; }
    public String resultId() { return this.resultId; }
    public Status resultStatus() { return this.resultStatus; }
    public String failureReason() { return this.failureReason; }
    public boolean hasResult() { return this.resultStatus != Status.NONE; }
    public void consumeResult() { this.resultId = ""; this.resultStatus = Status.NONE; this.failureReason = ""; }
    public UUID target() { return this.target; }
    public double arrivalRadius() { return this.arrivalRadius; }
    public int ticksRemaining() { return this.ticksRemaining; }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Kind", this.kind.name());
        tag.putString("Status", this.status.name());
        tag.put("Location", this.location.write());
        tag.putString("Target", this.target.toString());
        tag.putString("Name", this.name);
        tag.putString("Scope", this.scope);
        tag.putString("Selector", this.selector);
        tag.putString("Id", this.id);
        tag.put("BlockTarget", this.blockTarget.write());
        tag.putString("ResultId", this.resultId);
        tag.putString("ResultStatus", this.resultStatus.name());
        tag.putString("FailureReason", this.failureReason);
        tag.putDouble("Speed", this.speed);
        tag.putDouble("ArrivalRadius", this.arrivalRadius);
        tag.putInt("TicksRemaining", this.ticksRemaining);
        tag.putBoolean("TerminalTriggerSent", this.terminalTriggerSent);
        return tag;
    }

    public static SceneDirective read(CompoundTag tag) {
        SceneDirective directive = new SceneDirective();
        directive.kind = Kind.fromValue(tag.getString("Kind"));
        directive.status = Status.fromValue(tag.getString("Status"));
        if (tag.contains("Location")) directive.location = new DimBlockPos(tag.getCompound("Location"));
        if (tag.contains("Target")) {
            try { directive.target = UUID.fromString(tag.getString("Target")); } catch (IllegalArgumentException ignored) { }
        }
        directive.name = tag.getString("Name");
        directive.scope = tag.getString("Scope");
        directive.selector = tag.getString("Selector");
        directive.id = tag.getString("Id");
        if (tag.contains("BlockTarget")) directive.blockTarget = new DimBlockPos(tag.getCompound("BlockTarget"));
        directive.resultId = tag.getString("ResultId");
        directive.resultStatus = Status.fromValue(tag.getString("ResultStatus"));
        directive.failureReason = tag.getString("FailureReason");
        directive.speed = Math.max(0.05D, tag.getDouble("Speed"));
        directive.arrivalRadius = Math.max(0.25D, tag.getDouble("ArrivalRadius"));
        directive.ticksRemaining = Math.max(0, tag.getInt("TicksRemaining"));
        directive.terminalTriggerSent = tag.getBoolean("TerminalTriggerSent");
        return directive;
    }

    public enum Kind {
        NONE, LOCATION, ENTITY, BLOCK;
        static Kind fromValue(String value) {
            try { return valueOf(value.toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException exception) { return NONE; }
        }
    }

    public enum Status {
        NONE, ACTIVE, ARRIVED, UNREACHABLE, TIMED_OUT, CANCELLED;
        static Status fromValue(String value) {
            try { return valueOf(value.toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException exception) { return NONE; }
        }
    }
}
