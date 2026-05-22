package block_party.items;

import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.network.CustomMessenger;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class YearbookItem extends Item {
    public YearbookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        this.open(player, hand, -1L);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return InteractionResult.PASS;
        }
        if (entity instanceof Moe moe && player.getUUID().equals(moe.getOwnerUUID())) {
            this.open(player, hand, moe.getDatabaseID());
            return InteractionResult.SUCCESS;
        }
        return entity instanceof Moe ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    private void open(Player player, InteractionHand hand, long selectedId) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        BlockPartyDB db = BlockPartyDB.get(player.level());
        List<Long> ids = db.listNpcIds(player.getUUID());
        if (ids.isEmpty()) {
            return;
        }
        long id = selectedId < 0L ? ids.get(0) : selectedId;
        PacketDistributor.sendToPlayer(serverPlayer, CustomMessenger.yearbookOpenPayload(db, player.getUUID(), id, hand));
    }
}
