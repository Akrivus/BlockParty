package moeblocks.util;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class CellPhoneTeleporter implements ITeleporter {
    private final double x;
    private final double y;
    private final double z;
    
    public CellPhoneTeleporter(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld world, Function<ServerWorld, PortalInfo> info) {
        return new PortalInfo(new Vector3d(this.x, this.y, this.z), Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch);
    }
}
