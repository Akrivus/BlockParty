package moe.blocks.mod.entity.emotion;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.goal.RevengeGoal;
import moe.blocks.mod.entity.util.Emotions;
import moe.blocks.mod.entity.util.VoiceLines;
import moe.blocks.mod.entity.dating.DatingState;
import moe.blocks.mod.util.sort.SorterDistance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PsychoticEmotion extends AbstractEmotion {
    protected MurderGoal murderGoal;

    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_PSYCHOTIC.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.PSYCHOTIC;
    }

    @Override
    public void start() {
        this.entity.targetSelector.addGoal(7, this.murderGoal = new MurderGoal(this.entity));
    }

    @Override
    public void tick() {
        this.entity.world.addParticle(ParticleTypes.EFFECT, this.entity.getCenteredRandomPosX(), this.entity.getCenteredRandomPosY(), this.entity.getPosZRandom(this.entity.getWidth() / 2.0F), 0.0F, this.entity.getGaussian(0.2D), 0.0F);
        super.tick();
    }

    @Override
    public void stop() {
        this.entity.targetSelector.removeGoal(this.murderGoal);
    }

    @Override
    public boolean isArmed() {
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        DatingState datingState = this.entity.getDatingState();
        return !(datingState.get(target).canDieFor() || datingState.isFavorite(target));
    }

    static class MurderGoal extends RevengeGoal {

        public MurderGoal(FiniteEntity entity) {
            super(entity);
        }

        @Override
        public boolean preCheckTarget() {
            List<LivingEntity> victims = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F)).stream().filter(victim -> this.entity.canAttack(victim) && this.entity.isSuperiorTo(victim)).sorted(new SorterDistance(this.entity)).collect(Collectors.toList());
            this.victim = victims.isEmpty() ? null : victims.get(0);
            return true;
        }
    }
}
