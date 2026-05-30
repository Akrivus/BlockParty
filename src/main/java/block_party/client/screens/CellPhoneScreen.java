package block_party.client.screens;

import block_party.BlockParty;
import block_party.client.ClientTranslations;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcCallRequestPayload;
import block_party.network.payload.NpcDetailPayload;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class CellPhoneScreen extends ControllerScreen {
    public static final ResourceLocation CELL_PHONE_TEXTURE = BlockParty.source("textures/gui/cell_phone.png");
    private static final int WIDTH = 108;
    private static final int HEIGHT = 182;

    private final Map<Long, NpcDetailPayload> contacts = new LinkedHashMap<>();
    private final List<Button> contactButtons = new ArrayList<>();
    private final PhoneMotion motion = new PhoneMotion();
    private int start;
    private Button doneButton;
    private Button scrollUp;
    private Button scrollDown;
    private boolean forceClose;
    private DialogueOpenPayload pendingDialogue;

    public CellPhoneScreen(List<NpcDetailPayload> npcs) {
        super(npcs, -1L);
    }

    @Override
    protected void init() {
        this.forceClose = false;
        this.pendingDialogue = null;
        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.startClosing())
                .bounds(this.absoluteCenter(54), 190, WIDTH, 20)
                .build());
        this.scrollUp = this.addRenderableWidget(new ScrollButton(this.absoluteCenter(-37), 32, -1, button -> this.scroll(-1)));
        this.scrollDown = this.addRenderableWidget(new ScrollButton(this.absoluteCenter(-37), 91, 1, button -> this.scroll(1)));
        this.loadContacts();
        this.rebuildContacts();
    }

    @Override
    public void handleNpcCall(NpcCallPayload payload) {
        if (payload.success()) {
            this.startClosing();
        } else if (this.contacts.containsKey(payload.databaseId())) {
            this.motion.start(PhoneMotion.State.HORIZONTAL_SWING);
            this.play(BlockParty.source("item.cell_phone.dial"));
        }
    }

    public void openDialogueAfterClosing(DialogueOpenPayload payload) {
        this.pendingDialogue = payload;
        this.startClosing();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        float xOffset = this.motion.x(this.width, partialTick);
        float yOffset = this.motion.y(this.height, partialTick);
        float rotation = this.motion.rotation(partialTick);
        int adjustedMouseX = Math.round(mouseX - xOffset);
        int adjustedMouseY = Math.round(mouseY - yOffset);
        graphics.pose().pushPose();
        graphics.pose().translate(xOffset + this.width / 2.0F, yOffset + 2.0F + HEIGHT / 2.0F, 0.0F);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        graphics.pose().translate(-this.width / 2.0F, -2.0F - HEIGHT / 2.0F, 0.0F);
        graphics.blit(RenderType::guiTextured, CELL_PHONE_TEXTURE, this.left(WIDTH), 2, 0.0F, 0.0F, WIDTH, HEIGHT, 256, 256);
        this.renderScrollBar(graphics);
        this.renderPhoneWidgets(graphics, adjustedMouseX, adjustedMouseY, partialTick);
        graphics.pose().popPose();
        if (this.doneButton != null) {
            this.doneButton.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderScrollBar(GuiGraphics graphics) {
        int denominator = Math.max(1, this.contacts.size() - this.contacts.size() % 4);
        int y = (int) (Math.min((double) this.start / denominator, 1.0D) * 35.0D);
        graphics.blit(RenderType::guiTextured, CELL_PHONE_TEXTURE, this.absoluteCenter(-37), 40 + y, 108, 82, 7, 15, 256, 256);
    }

    private void loadContacts() {
        this.contacts.clear();
        for (NpcDetailPayload npc : this.npcs) {
            if (npc.found()) {
                this.contacts.put(npc.databaseId(), npc);
            }
        }
        if (this.contacts.isEmpty()) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.translatable("gui.block_party.error.empty"), true);
            }
            this.forceClose = true;
            this.onClose();
        }
    }

    private void rebuildContacts() {
        for (Button button : this.contactButtons) {
            this.removeWidget(button);
        }
        this.contactButtons.clear();
        List<NpcDetailPayload> values = List.copyOf(this.contacts.values());
        for (int offset = 0; offset < 4 && this.start + offset < values.size(); ++offset) {
            NpcDetailPayload npc = values.get(this.start + offset);
            Button button = new ContactButton(this.absoluteCenter(45), 32 + offset * 17, npc, value -> this.call(npc.databaseId()));
            this.contactButtons.add(this.addRenderableWidget(button));
        }
        if (this.scrollUp != null) {
            this.scrollUp.visible = this.contacts.size() > 4;
        }
        if (this.scrollDown != null) {
            this.scrollDown.visible = this.contacts.size() > 4;
        }
    }

    private void renderPhoneWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.scrollUp != null) {
            this.scrollUp.render(graphics, mouseX, mouseY, partialTick);
        }
        if (this.scrollDown != null) {
            this.scrollDown.render(graphics, mouseX, mouseY, partialTick);
        }
        for (Button button : this.contactButtons) {
            button.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void scroll(int delta) {
        if (this.contacts.isEmpty()) {
            this.start = 0;
            return;
        }
        this.motion.start(PhoneMotion.State.VERTICAL_SWING);
        this.start += 4 * delta;
        int range = this.contacts.size() - 1;
        if (this.start < 0) {
            this.start = range - range % 4;
        }
        if (this.start > range) {
            this.start = 0;
        }
        this.rebuildContacts();
    }

    private void call(long databaseId) {
        this.motion.start(PhoneMotion.State.VIBRATE);
        this.play(BlockParty.source("item.cell_phone.button"));
        PacketDistributor.sendToServer(new NpcCallRequestPayload(databaseId));
    }

    private void startClosing() {
        this.motion.start(PhoneMotion.State.SLIDE_OUT);
    }

    @Override
    public void tick() {
        super.tick();
        this.motion.tick();
        if (this.motion.doneClosing()) {
            this.forceClose = true;
            if (this.pendingDialogue != null && this.minecraft != null) {
                this.minecraft.setScreen(new DialogueScreen(this.pendingDialogue));
            } else {
                this.onClose();
            }
        }
    }

    @Override
    public void onClose() {
        if (this.forceClose) {
            super.onClose();
            return;
        }
        this.startClosing();
    }

    private void play(ResourceLocation soundId) {
        if (this.minecraft == null) {
            return;
        }
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
        if (sound != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.startClosing();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            this.scroll(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            this.scroll(1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) {
            this.scroll(-1);
        } else if (scrollY < 0) {
            this.scroll(1);
        }
        return scrollY != 0.0D;
    }

    private static final class PhoneMotion {
        private static final int SHORT_DURATION = 16;
        private static final int CLOSE_DURATION = 18;

        private State state = State.NONE;
        private int tick;

        void start(State state) {
            this.state = state;
            this.tick = 0;
        }

        void tick() {
            if (this.state == State.NONE) {
                return;
            }
            ++this.tick;
            if (this.state != State.SLIDE_OUT && this.tick >= SHORT_DURATION) {
                this.start(State.NONE);
            }
        }

        boolean doneClosing() {
            return this.state == State.SLIDE_OUT && this.tick >= CLOSE_DURATION;
        }

        float x(int screenWidth, float partialTick) {
            float age = this.tick + partialTick;
            return switch (this.state) {
                case HORIZONTAL_SWING -> (float) Math.sin(age * 0.7F) * 5.0F;
                case VIBRATE -> ((int) age % 2 == 0 ? -1.0F : 1.0F) * 2.0F;
                default -> 0.0F;
            };
        }

        float y(int screenHeight, float partialTick) {
            float age = this.tick + partialTick;
            return switch (this.state) {
                case VERTICAL_SWING -> (float) Math.sin(age * 0.65F) * 5.0F;
                case SLIDE_OUT -> ease(Math.min(age / CLOSE_DURATION, 1.0F)) * (screenHeight / 2.0F + HEIGHT);
                default -> 0.0F;
            };
        }

        float rotation(float partialTick) {
            float age = this.tick + partialTick;
            return switch (this.state) {
                case HORIZONTAL_SWING -> (float) Math.sin(age * 0.7F) * 2.0F;
                case VERTICAL_SWING -> (float) Math.sin(age * 0.65F) * 1.0F;
                case VIBRATE -> ((int) age % 2 == 0 ? -1.0F : 1.0F) * 2.5F;
                case SLIDE_OUT -> ease(Math.min(age / CLOSE_DURATION, 1.0F)) * 12.0F;
                default -> 0.0F;
            };
        }

        private static float ease(float value) {
            return value * value * (3.0F - 2.0F * value);
        }

        enum State {
            NONE,
            HORIZONTAL_SWING,
            VERTICAL_SWING,
            VIBRATE,
            SLIDE_OUT
        }
    }

    private static class ContactButton extends Button {
        private final boolean reachable;

        ContactButton(int x, int y, NpcDetailPayload npc, OnPress onPress) {
            super(x, y, 81, 15, ClientTranslations.displayName(npc), onPress, DEFAULT_NARRATION);
            this.reachable = isReachable(npc);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.blit(RenderType::guiTextured, CELL_PHONE_TEXTURE, this.getX(), this.getY(), 108, this.isHoveredOrFocused() ? 98 : 115, this.width, this.height, 256, 256);
            int color = this.reachable ? this.isHoveredOrFocused() ? 0xFFFFFF : 0xFF7FB6 : 0x777777;
            graphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 10, this.getY() + 4, color, false);
        }

        private static boolean isReachable(NpcDetailPayload npc) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) {
                return false;
            }
            return !npc.dead() && !npc.hiding();
        }
    }

    private static class ScrollButton extends Button {
        ScrollButton(int x, int y, int delta, OnPress onPress) {
            super(x, y, 7, 7, Component.literal(String.valueOf(delta)), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int textureX = this.isHoveredOrFocused() ? 116 : 108;
            graphics.blit(RenderType::guiTextured, CELL_PHONE_TEXTURE, this.getX(), this.getY(), textureX, 73, this.width, this.height, 256, 256);
        }
    }
}
