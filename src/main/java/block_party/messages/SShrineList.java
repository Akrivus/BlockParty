package block_party.messages;

import block_party.client.ShrineLocation;
import block_party.db.BlockPartyDB;
import block_party.db.sql.Row;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SShrineList extends AbstractMessage.Server {
    protected List<BlockPos> shrines = new ArrayList<>();

    public SShrineList(Player player, ResourceKey<Level> dim) {
        BlockPartyDB.Shrines.all().forEach((shrine) -> {
            if (player.getUUID().equals(shrine.get(Row.PLAYER_UUID)) || shrine.isDim(dim)) {
                this.shrines.add(shrine.getPos());
            }
        });
    }

    public SShrineList(FriendlyByteBuf buffer) {
        int length = buffer.readInt();
        for (int i = 0; i < length; ++i) {
            this.shrines.add(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.shrines.size());
        this.shrines.forEach((pos) -> {
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        });
    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        BlockPartyDB.ShrineLocation = new ShrineLocation(this.shrines);
    }
}
