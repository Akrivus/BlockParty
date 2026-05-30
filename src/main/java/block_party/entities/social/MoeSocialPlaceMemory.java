package block_party.entities.social;

import block_party.entities.environment.MoePlaceMemory;
import net.minecraft.core.BlockPos;

import java.util.UUID;

public record MoeSocialPlaceMemory(
        UUID owner,
        String ownerName,
        MoePlaceMemory.PlaceType type,
        BlockPos pos,
        MoeSocialRules.SocialPlaceBehavior behavior,
        MoeSocialRules.SocialSignal signal,
        double score) {
}
