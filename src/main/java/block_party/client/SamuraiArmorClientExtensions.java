package block_party.client;

import block_party.BlockParty;
import block_party.client.model.SamuraiModel;
import block_party.items.MaskedSamuraiItem;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class SamuraiArmorClientExtensions implements IClientItemExtensions {
    private final String texture;

    private SamuraiArmorClientExtensions(String texture) {
        this.texture = texture;
    }

    public static void register(RegisterClientExtensionsEvent event, Item item) {
        String texture = item instanceof MaskedSamuraiItem ? "samurai_mask" : "samurai";
        event.registerItem(new SamuraiArmorClientExtensions(texture), item);
    }

    @Override
    public Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model original) {
        if (layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS) {
            return SamuraiModel.getArmorModel(EquipmentSlot.LEGS);
        }
        return SamuraiModel.getArmorModel(EquipmentSlot.CHEST);
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, ResourceLocation fallback) {
        int layerIndex = type == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS ? 2 : 1;
        return BlockParty.source("textures/models/armor/" + this.texture + "_layer_" + layerIndex + ".png");
    }
}
