package com.juststudio.justhud.gui;

import com.juststudio.justhud.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.input.KeyEvent;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int MARGIN = 10;

    public ConfigScreen(Screen parentScreen) {
        super(Component.translatable("justhud.menu.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        this.addRenderableWidget(Button.builder(
                Component.translatable("justhud.menu.key_binds"),
                btn -> Minecraft.getInstance().setScreen(new BindSettingsScreen(this))
            ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build()
        );
        
        this.addRenderableWidget(Button.builder(
                Component.translatable("justhud.menu.visual_settings"),
                btn -> Minecraft.getInstance().setScreen(new VisualSettingsScreen(this))
            ).bounds(centerX - BUTTON_WIDTH / 2, startY + BUTTON_HEIGHT + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build()
        );
        
        this.addRenderableWidget(Button.builder(
                Component.translatable("justhud.menu.back"),
                btn -> this.onClose()
            ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 30, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build()
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // Avoid Minecraft's blur pass: it can be invoked twice in one frame with OptiFine.
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        
        graphics.drawCenteredString(this.font, 
            Component.literal("JustHUD • Just Studio"), 
            this.width / 2, 20, 0xFFFFFF);
        
        graphics.drawCenteredString(this.font, 
            Component.literal("§7Настройка интерфейса и возможностей мода"), 
            this.width / 2, 40, 0x808080);
        
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parentScreen);
    }
}