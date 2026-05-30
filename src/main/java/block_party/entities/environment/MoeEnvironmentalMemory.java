package block_party.entities.environment;

import block_party.entities.Moe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public final class MoeEnvironmentalMemory {
    private final Moe moe;
    private int observationDelay;
    private int observationMemoryTicks;
    private MoeEnvironmentalObservation.Observation observation = MoeEnvironmentalObservation.Observation.none();
    private MoePlaceMemory.Place rememberedPlace = MoePlaceMemory.Place.none();

    public MoeEnvironmentalMemory(Moe moe) {
        this.moe = moe;
    }

    public void readRememberedPlace(CompoundTag compound, String key) {
        this.rememberedPlace = compound.contains(key)
                ? MoePlaceMemory.Place.read(compound.getCompound(key))
                : MoePlaceMemory.Place.none();
    }

    public void writeRememberedPlace(CompoundTag compound, String key) {
        if (this.rememberedPlace.type() != MoePlaceMemory.PlaceType.NONE) {
            compound.put(key, this.rememberedPlace.write());
        }
    }

    public MoeEnvironmentalRules.ShelterScore shelterScoreAt(BlockPos feetPos) {
        return MoeEnvironmentalRules.shelterScore(this.moe.level(), feetPos);
    }

    public Optional<MoeEnvironmentalObservation.Observation> observeEnvironmentNow() {
        Optional<MoeEnvironmentalObservation.Observation> scanned = MoeEnvironmentalObservation.scan(this.moe);
        scanned.ifPresent(this::rememberObservation);
        return scanned;
    }

    public Optional<MoeEnvironmentalObservation.Observation> latestObservation() {
        return this.observationMemoryTicks > 0 && this.observation.kind() != MoeEnvironmentalObservation.Kind.NONE
                ? Optional.of(this.observation)
                : Optional.empty();
    }

    public Optional<MoePlaceMemory.Place> observePlaceNow() {
        Optional<MoePlaceMemory.Place> place = MoePlaceMemory.scan(this.moe);
        place.ifPresent(this::rememberPlace);
        return place;
    }

    public Optional<MoePlaceMemory.Place> rememberedPlace() {
        return this.rememberedPlace.type() == MoePlaceMemory.PlaceType.NONE
                ? Optional.empty()
                : Optional.of(this.rememberedPlace);
    }

    public void rememberPlace(MoePlaceMemory.Place place) {
        if (place.type() == MoePlaceMemory.PlaceType.NONE || place.overcrowded()) {
            return;
        }
        if (this.rememberedPlace.type() == MoePlaceMemory.PlaceType.NONE
                || place.score() >= this.rememberedPlace.score() - 8.0D
                || !MoePlaceMemory.stillValid(this.moe, this.rememberedPlace)) {
            this.rememberedPlace = place;
        }
    }

    public void clearRememberedPlace() {
        this.rememberedPlace = MoePlaceMemory.Place.none();
    }

    public void tick() {
        if (this.observationMemoryTicks > 0) {
            --this.observationMemoryTicks;
            if (this.observationMemoryTicks <= 0) {
                this.observation = MoeEnvironmentalObservation.Observation.none();
            }
        }
        if (this.shouldSkipObservation()) {
            return;
        }
        if (this.observationDelay > 0) {
            --this.observationDelay;
            return;
        }
        this.observationDelay = 40 + this.moe.getRandom().nextInt(60);
        this.observeEnvironmentNow();
        this.observePlaceNow();
    }

    private boolean shouldSkipObservation() {
        return this.moe.level().isClientSide || this.moe.hasDialogue() || this.moe.isPassenger();
    }

    private void rememberObservation(MoeEnvironmentalObservation.Observation observation) {
        this.observation = observation;
        this.observationMemoryTicks = 20 * 20;
        this.moe.getLookControl().setLookAt(
                observation.pos().getX() + 0.5D,
                observation.pos().getY() + 0.5D,
                observation.pos().getZ() + 0.5D,
                30.0F,
                30.0F);
        this.moe.setTemporaryAnimationKey(MoeEnvironmentalObservation.animationFor(observation.kind()), 48);
        if (observation.kind() == MoeEnvironmentalObservation.Kind.TENSION) {
            this.moe.addStress(0.02F);
        } else if (observation.kind() == MoeEnvironmentalObservation.Kind.AFFINITY) {
            this.moe.addRelaxation(0.02F);
        }
    }
}
