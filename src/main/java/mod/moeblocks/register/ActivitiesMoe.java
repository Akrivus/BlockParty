package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ActivitiesMoe {
    public static final DeferredRegister<Activity> REGISTRY = DeferredRegister.create(ForgeRegistries.ACTIVITIES, MoeMod.ID);
}
