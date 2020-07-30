package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MemoryModuleTypesMoe {
    public static final DeferredRegister<MemoryModuleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, MoeMod.ID);
}
