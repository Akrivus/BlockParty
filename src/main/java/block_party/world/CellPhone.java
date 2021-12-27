package block_party.world;

import block_party.db.records.NPC;
import block_party.npc.BlockPartyNPC;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class CellPhone implements ITeleporter {
    private final double x;
    private final double y;
    private final double z;
    private final NPC npc;
    private final ServerPlayer player;

    public CellPhone(NPC npc, ServerPlayer player) {
        this.x = player.getX() - Math.sin(0.0174532925F * player.getYRot()) * 1.44;
        this.y = player.getY();
        this.z = player.getZ() + Math.cos(0.0174532925F * player.getYRot()) * 1.44;
        this.npc = npc;
        this.player = player;
    }

    public BlockPartyNPC call() {
        return this.npc.getServerEntity(this.player.getServer());
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel level, Function<ServerLevel, PortalInfo> info) {
        return new PortalInfo(new Vec3(this.x, this.y, this.z), Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }
}
