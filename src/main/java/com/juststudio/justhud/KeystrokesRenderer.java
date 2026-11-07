package com.juststudio.justhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class KeystrokesRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static boolean wPressed = false;
    private static boolean aPressed = false;
    private static boolean sPressed = false;
    private static boolean dPressed = false;
    
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS || event.getAction() == GLFW.GLFW_RELEASE) {
            updateKeyStates();
        }
    }
    
    public static void register() {
        InputEvent.Key.BUS.addListener(KeystrokesRenderer::onKeyInput);
    }

    private static void updateKeyStates() {
        wPressed = mc.options.keyUp.isDown();
        aPressed = mc.options.keyLeft.isDown();
        sPressed = mc.options.keyDown.isDown();
        dPressed = mc.options.keyRight.isDown();
    }
    
    public static void render(GuiGraphics graphics) {
        if (!Config.SHOW_KEYSTROKES.get()) return;
        renderKeystrokes(graphics);
    }
    
    private static void renderKeystrokes(GuiGraphics graphics) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        int baseX = Config.KEYSTROKES_X.get();
        int baseY = Config.KEYSTROKES_Y.get();
        
        if (baseX < 0) baseX = screenWidth + baseX;
        if (baseY < 0) baseY = screenHeight + baseY;
        
        int keySize = 20;
        int spacing = 2;
        
        renderKey(graphics, "W", baseX + keySize + spacing, baseY, keySize, wPressed);
        renderKey(graphics, "A", baseX, baseY + keySize + spacing, keySize, aPressed);
        renderKey(graphics, "S", baseX + keySize + spacing, baseY + keySize + spacing, keySize, sPressed);
        renderKey(graphics, "D", baseX + (keySize + spacing) * 2, baseY + keySize + spacing, keySize, dPressed);
    }
    
    private static void renderKey(GuiGraphics graphics, String key, int x, int y, int size, boolean pressed) {
        Color bgColor = pressed ? new Color(255, 255, 255, 150) : new Color(0, 0, 0, 100);
        Color textColor = pressed ? Color.BLACK : Color.WHITE;
        
        graphics.fill(x, y, x + size, y + size, bgColor.getRGB());
        
        graphics.fill(x, y, x + size, y + 1, Color.GRAY.getRGB());
        graphics.fill(x, y + size - 1, x + size, y + size, Color.GRAY.getRGB());
        graphics.fill(x, y, x + 1, y + size, Color.GRAY.getRGB());
        graphics.fill(x + size - 1, y, x + size, y + size, Color.GRAY.getRGB());
        
        Font font = mc.font;
        int textWidth = font.width(key);
        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - 8) / 2;
        
        graphics.drawString(font, key, textX, textY, textColor.getRGB());
    }
}