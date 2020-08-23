package mod.moeblocks.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.moeblocks.MoeMod;
import mod.moeblocks.init.MoeItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CellPhoneItem extends Item {

    public CellPhoneItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE));
    }

    @Mod.EventBusSubscriber(modid = MoeMod.ID)
    public static class CellPhoneHandler {
        @SubscribeEvent
        public static void onRenderHand(RenderHandEvent e) {
            ItemStack stack = e.getItemStack();
            if (stack.getItem() instanceof CellPhoneItem) {
                float degrees = (float) Math.sin(e.getPartialTicks()) * 2.0F;
                MatrixStack matrix = e.getMatrixStack();
                matrix.rotate(Vector3f.YP.rotationDegrees(degrees));
            }
        }
    }
}
