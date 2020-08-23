package mod.moeblocks.init;

import mod.moeblocks.MoeMod;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeSchedules {
    public static final DeferredRegister<Schedule> REGISTRY = DeferredRegister.create(ForgeRegistries.SCHEDULES, MoeMod.ID);
}
