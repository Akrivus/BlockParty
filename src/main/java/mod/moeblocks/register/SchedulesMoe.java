package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SchedulesMoe {
    public static final DeferredRegister<Schedule> REGISTRY = DeferredRegister.create(ForgeRegistries.SCHEDULES, MoeMod.ID);
}
