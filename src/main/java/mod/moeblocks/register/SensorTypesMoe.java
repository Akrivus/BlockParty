package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SensorTypesMoe {
    public static final DeferredRegister<SensorType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MoeMod.ID);
}
