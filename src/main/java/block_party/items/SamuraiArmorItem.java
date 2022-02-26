package block_party.items;

import block_party.BlockParty;
import block_party.client.model.SamuraiModel;
import block_party.registry.CustomItems;
import block_party.registry.CustomTags;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class SamuraiArmorItem extends ArmorItem {

    public SamuraiArmorItem(EquipmentSlot slot) {
        super(new ArmorMaterial() {
            @Override
            public int getDurabilityForSlot(EquipmentSlot slot) {
                switch (slot) {
                case HEAD:
                    return 468;
                case CHEST:
                    return 540;
                case LEGS:
                    return 576;
                case FEET:
                    return 396;
                default:
                    return 0;
                }
            }

            @Override
            public int getDefenseForSlot(EquipmentSlot slot) {
                switch (slot) {
                case HEAD:
                    return 4;
                case CHEST:
                    return 6;
                case LEGS:
                    return 8;
                case FEET:
                    return 2;
                default:
                    return 0;
                }
            }

            @Override
            public int getEnchantmentValue() {
                return 19;
            }

            @Override
            public float getToughness() {
                return 4.0F;
            }

            @Override
            public float getKnockbackResistance() {
                return 0.4F;
            }

            @Override
            public SoundEvent getEquipSound() {
                return SoundEvents.ARMOR_EQUIP_CHAIN;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }

            @Override
            public String getName() {
                return BlockParty.source("samurai").toString();
            }
        }, slot, new Properties().tab(BlockParty.CreativeModeTab).stacksTo(1));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderman) {
        return stack.getItem() == CustomItems.SAMURAI_KABUTO.get();
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> renderer) {
        renderer.accept(new IItemRenderProperties() {
            @Override
            public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, A _default) {
                return (A) SamuraiModel.getArmorModel(slot);
            }

            @Override
            public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTicks) {
                Window window = Minecraft.getInstance().getWindow();
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, BlockParty.source("textures/misc/samurai.png"));
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferbuilder.vertex(0.0D, window.getGuiScaledHeight(), -90.0D).uv(0.0F, 1.0F).endVertex();
                bufferbuilder.vertex(window.getGuiScaledWidth(), window.getGuiScaledHeight(), -90.0D).uv(1.0F, 1.0F).endVertex();
                bufferbuilder.vertex(window.getGuiScaledWidth(), 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
                bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
                tesselator.end();
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        });
    }

    @SubscribeEvent
    public static void onPickupXP(PlayerXpEvent.PickupXp e) {
        ExperienceOrb orb = e.getOrb();
        if (e.getPlayer() instanceof ServerPlayer player) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = player.getItemBySlot(slot);
                if (stack.is(CustomTags.Items.SAMURAI_ITEMS)) {
                    int repair = Math.min(orb.getValue(), stack.getDamageValue());
                    stack.setDamageValue(stack.getDamageValue() - repair);
                }
            }
        }
    }
}
