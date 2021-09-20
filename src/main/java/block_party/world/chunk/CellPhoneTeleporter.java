package block_party.world.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
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
    public PortalInfo getPortalInfo(Entity entity, ServerLevel level, Function<ServerLevel, PortalInfo> info) {
        return new PortalInfo(new Vec3(this.x, this.y, this.z), Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }
}
