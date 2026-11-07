package com.juststudio.justhud.gui;

import com.juststudio.justhud.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class VisualSettingsScreen extends Screen {
    private final Screen parentScreen;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int MARGIN = 5;

    public VisualSettingsScreen(Screen parentScreen) {
        super(Component.literal("Настройки интерфейса"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 60;

        addToggleButton("Отображение FPS", Config.SHOW_FPS, centerX, startY, 0);
        addToggleButton("Координаты", Config.SHOW_COORDINATES, centerX, startY, 1);
        addToggleButton("Отображение CPS", Config.SHOW_CPS, centerX, startY, 2);
        addToggleButton("Прочность брони", Config.SHOW_ARMOR, centerX, startY, 3);
        addToggleButton("Информация о сущности", Config.SHOW_ENTITY_INFO, centerX, startY, 4);
        addToggleButton("Нажатия клавиш", Config.SHOW_KEYSTROKES, centerX, startY, 5);

        this.addRenderableWidget(Button.builder(
                Component.literal("Сбросить расположение"),
                btn -> resetPositions()
            ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + MARGIN) * 7, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build()
        );

        this.addRenderableWidget(Button.builder(
                Component.literal("Назад"),
                btn -> this.onClose()
            ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 30, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build()
        );
    }

    private void addToggleButton(String name, ForgeConfigSpec.BooleanValue value, int centerX, int startY, int index) {
        this.addRenderableWidget(Button.builder(
                getToggleText(name, value.get()),
                btn -> {
                    value.set(!value.get());
                    btn.setMessage(getToggleText(name, value.get()));
                }
            ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + MARGIN) * index, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build()
        );
    }

    private Component getToggleText(String name, boolean status) {
        if (status) {
            return Component.literal(name + ": §aВКЛ");
        } else {
            return Component.literal(name + ": §cВЫКЛ");
        }
    }

    private void resetPositions() {
        Config.FPS_X.set(10);
        Config.FPS_Y.set(10);
        Config.COORDINATES_X.set(10);
        Config.COORDINATES_Y.set(25);
        Config.CPS_X.set(10);
        Config.CPS_Y.set(40);
        Config.ARMOR_X.set(-40);
        Config.ARMOR_Y.set(-100);
        Config.ENTITY_INFO_X.set(0);
        Config.ENTITY_INFO_Y.set(0);
        Config.KEYSTROKES_X.set(10);
        Config.KEYSTROKES_Y.set(-100);
        Config.SPEC.save();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        
        graphics.drawCenteredString(this.font, 
            Component.literal("Настройки интерфейса"), 
            this.width / 2, 20, 0xFFFFFF);
        
        graphics.drawCenteredString(this.font, 
            Component.literal("§7Включайте и отключайте элементы HUD"), 
            this.width / 2, 40, 0x808080);
        
        graphics.drawString(this.font, 
            Component.literal("Дополнительные параметры: config/justhud.toml"), 
            10, this.height - 40, 0xFFFF00);
        
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
        Config.SPEC.save();
        Minecraft.getInstance().setScreen(parentScreen);
    }
}