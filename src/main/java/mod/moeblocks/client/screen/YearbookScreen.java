package mod.moeblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.moeblocks.MoeMod;
import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.item.YearbookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class YearbookScreen extends Screen {
    public static final ResourceLocation YEARBOOK_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/yearbook.png");
    private final ItemStack stack;
    private YearbookScreen.ChangePageButton buttonNextPage;
    private YearbookScreen.ChangePageButton buttonPrevPage;
    private String name;
    private String pos;
    private String[] stats = new String[5];
    private String[] lines = new String[3];
    private StateEntity entity;
    private UUID player;
    private int page;

    public YearbookScreen(ItemStack stack, UUID player) {
        this(stack, player, 0);
    }

    public YearbookScreen(ItemStack stack, UUID player, int page) {
        super(NarratorChatListener.EMPTY);
        this.stack = stack;
        this.player = player;
        this.page = page % this.getPageCount();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderPortrait(stack);
        this.renderEntity(this.width / 2, 90, 40.0F, 0, 0, this.entity);
        this.renderBook(stack);
        this.font.drawString(stack, this.name, (this.width - this.font.getStringWidth(this.name)) / 2, 92, 0);
        this.font.drawString(stack, this.pos, (this.width - this.font.getStringWidth(this.pos)) / 2, 123, 0);
        for (int x = 0; x < this.stats.length; ++x) {
            this.font.drawString(stack, this.stats[x], this.width / 2 - 40 + x * 22 - this.font.getStringWidth(this.stats[x]), 105, 0);
        }
        for (int y = 0; y < this.lines.length; ++y) {
            this.font.drawString(stack, this.lines[y], this.width / 2 - 47, 134 + 10 * y , 0);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public void renderPortrait(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, (this.width - 60) / 2, 28, 183, 26, 58, 58);
    }

    public void renderBook(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, (this.width - 146) / 2, 2, 20, 0, 146, 188);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            switch (keyCode) {
            case GLFW.GLFW_KEY_RIGHT:
            case GLFW.GLFW_KEY_D:
                this.buttonNextPage.onPress();
                return true;
            case GLFW.GLFW_KEY_LEFT:
            case GLFW.GLFW_KEY_A:
                this.buttonPrevPage.onPress();
                return true;
            default:
                return false;
            }
        }
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, 196, 200, 20, DialogTexts.field_240632_c_, (button) -> this.minecraft.displayGuiScreen(null)));
        this.buttonNextPage = this.addButton(new ChangePageButton((this.width - 146) / 2 + 108, 52, 1, (button) -> this.nextPage()));
        this.buttonPrevPage = this.addButton(new ChangePageButton((this.width - 146) / 2 + 29, 52, -1, (button) -> this.prevPage()));
        this.updateButtons();
    }

    public void renderEntity(int posX, int posY, float scale, float mouseX, float mouseY, LivingEntity entity) {
        this.entity.rotationYaw = this.entity.rotationYawHead = 180.0F;
        this.entity.setPosition(this.minecraft.player.getPosX(), this.minecraft.player.getPosY(), this.minecraft.player.getPosZ());
        Quaternion forward = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion lateral = Vector3f.XP.rotationDegrees(0.0F);
        forward.multiply(lateral);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(posX, posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack stack = new MatrixStack();
        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale(scale, scale, scale);
        stack.rotate(forward);
        EntityRendererManager renderer = this.minecraft.getRenderManager();
        renderer.setCameraOrientation(lateral);
        renderer.setRenderShadow(false);
        IRenderTypeBuffer.Impl buffer = this.minecraft.getRenderTypeBuffers().getBufferSource();
        renderer.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, buffer, 15728880);
        buffer.finish();
        renderer.setRenderShadow(true);
        RenderSystem.popMatrix();
    }

    protected void nextPage() {
        if (this.page < this.getPageCount() - 1) {
            ++this.page;
        }
        this.updateButtons();
    }

    private int getPageCount() {
        return YearbookItem.getPageCount(this.stack);
    }

    private void updateButtons() {
        this.entity = YearbookItem.getPage(this.minecraft.world, this.stack, this.page);
        this.name = String.format("%s", this.entity.getPlainName());
        this.pos = String.format("%.0f %.0f %.0f", this.entity.getPosX(), this.entity.getPosY(), this.entity.getPosZ());
        this.stats[0] = String.format("%.0f", this.entity.getHealth());
        this.stats[1] = String.format("%.0f", this.entity.getFoodStats().getFoodLevel());
        this.stats[2] = String.format("%.0f", this.entity.getRelationships().get(this.player).getTrust());
        this.stats[3] = String.format("%.0f", this.entity.getRelationships().get(this.player).getAffection());
        this.stats[4] = String.format("%.0f", this.entity.getStressStats().getStress());
        this.lines[0] = String.format("%d years old", this.entity.getAgeInYears());
        this.lines[1] = String.format("Blood type %s", this.entity.getBloodType().toString());
        this.lines[2] = String.format("Seen %d days ago", this.entity.getRelationships().get(this.player).getLastSeen() / 24000);
        this.buttonNextPage.visible = this.page < this.getPageCount() - 1;
        this.buttonPrevPage.visible = this.page > 0;
    }

    protected void prevPage() {
        if (this.page > 0) {
            --this.page;
        }
        this.updateButtons();
    }

    @OnlyIn(Dist.CLIENT)
    public class ChangePageButton extends Button {
        private final int delta;

        public ChangePageButton(int x, int y, int delta, Button.IPressable button) {
            super(x, y, 7, 10, StringTextComponent.EMPTY, button);
            this.delta = delta;
        }

        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(YearbookScreen.YEARBOOK_TEXTURES);
            int x = this.delta > 0 ? 248 : 169;
            int y = this.isHovered() ? 36 : 64;
            this.blit(stack, this.x, this.y, x, y, 23, 13);
        }

        @Override
        public void playDownSound(SoundHandler soundHandler) {
            soundHandler.play(SimpleSound.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
        }
    }
}
