package block_party.message;

import block_party.client.screen.ControllerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.List;
import java.util.UUID;

public abstract class SOpenController extends SNPCList {
    protected final UUID id;
    protected final InteractionHand hand;
    protected ItemStack stack;

    public SOpenController(List<UUID> npcs, UUID id, InteractionHand hand) {
        super(npcs);
        this.id = id == null ? UUID.randomUUID() : id;
        this.hand = hand;
    }

    public SOpenController(FriendlyByteBuf buffer) {
        super(buffer);
        this.id = buffer.readUUID();
        this.hand = buffer.readEnum(InteractionHand.class);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeUUID(this.id);
        buffer.writeEnum(this.hand);
    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.player.getItemInHand(this.hand).getItem() != this.getItem()) { return; }
        if (minecraft.screen != null) { return; }
        minecraft.setScreen(this.getScreen());
    }

    protected abstract Item getItem();

    protected abstract ControllerScreen getScreen();
}
