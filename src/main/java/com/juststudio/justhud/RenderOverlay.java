package com.juststudio.justhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import java.awt.*;

public class RenderOverlay {
    private final Minecraft mc = Minecraft.getInstance();
    private long lastUpdate = 0;
    private int fps = 0;
    private int frameCount = 0;

    public void render(GuiGraphics graphics) {
        Font font = mc.font;

        updateFPS();

        if (Config.SHOW_FPS.get()) renderFPS(graphics, font);
        if (Config.SHOW_COORDINATES.get()) renderCoordinates(graphics, font);
        if (Config.SHOW_CPS.get()) renderCPS(graphics, font);
        if (Config.SHOW_ARMOR.get()) renderArmor(graphics);
    }

    private void updateFPS() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastUpdate = currentTime;
        }
    }

    private void renderFPS(GuiGraphics graphics, Font font) {
        String text = "FPS: " + fps;
        int x = Config.FPS_X.get();
        int y = Config.FPS_Y.get();
        renderTextWithBackground(graphics, font, text, x, y, getFPSColor(fps));
    }

    private void renderCoordinates(GuiGraphics graphics, Font font) {
        if (mc.player == null) return;
        String text = String.format("XYZ: %.1f, %.1f, %.1f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ());
        int x = Config.COORDINATES_X.get();
        int y = Config.COORDINATES_Y.get();
        renderTextWithBackground(graphics, font, text, x, y, Color.WHITE);
    }

    private void renderCPS(GuiGraphics graphics, Font font) {
        String text = String.format("CPS: %d | %d",
                ClientEventHandler.getLeftCPS(), ClientEventHandler.getRightCPS());
        int x = Config.CPS_X.get();
        int y = Config.CPS_Y.get();
        renderTextWithBackground(graphics, font, text, x, y, Color.WHITE);
    }

    private void renderArmor(GuiGraphics graphics) {
        if (mc.player == null) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int baseX = Config.ARMOR_X.get() >= 0 ? Config.ARMOR_X.get() : screenWidth + Config.ARMOR_X.get();
        int baseY = Config.ARMOR_Y.get() >= 0 ? Config.ARMOR_Y.get() : screenHeight + Config.ARMOR_Y.get();

        baseX -= 10;

        ItemStack helmet = mc.player.getInventory().getEquipment().get(EquipmentSlot.HEAD);
        ItemStack chestplate = mc.player.getInventory().getEquipment().get(EquipmentSlot.CHEST);
        ItemStack leggings = mc.player.getInventory().getEquipment().get(EquipmentSlot.LEGS);
        ItemStack boots = mc.player.getInventory().getEquipment().get(EquipmentSlot.FEET);

        int yOffset = 0;
        if (!helmet.isEmpty()) yOffset += renderArmorPiece(graphics, helmet, baseX, baseY + yOffset);
        if (!chestplate.isEmpty()) yOffset += renderArmorPiece(graphics, chestplate, baseX, baseY + yOffset);
        if (!leggings.isEmpty()) yOffset += renderArmorPiece(graphics, leggings, baseX, baseY + yOffset);
        if (!boots.isEmpty()) renderArmorPiece(graphics, boots, baseX, baseY + yOffset);
    }

    private int renderArmorPiece(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y);
        int maxDamage = stack.getMaxDamage();
        int damage = stack.getDamageValue();
        int durability = maxDamage > 0 ? (int) ((1.0f - (float) damage / maxDamage) * 100) : 100;
        String text = durability + "%";
        Font font = mc.font;
        renderTextWithBackground(graphics, font, text, x + 22, y + 4, getDurabilityColor(durability));
        return 20;
    }

    private void renderTextWithBackground(GuiGraphics graphics, Font font, String text, int x, int y, Color color) {
        int width = font.width(text);
        graphics.fill(x - 3, y - 1, x + width + 3, y + 10, 0x80000000);
        graphics.drawString(font, text, x, y, color.getRGB());
    }

    private Color getFPSColor(int fps) {
        if (fps >= 120) return Color.GREEN;
        if (fps >= 60) return Color.YELLOW;
        if (fps >= 30) return Color.ORANGE;
        return Color.RED;
    }

    private Color getDurabilityColor(int durability) {
        if (durability >= 70) return Color.GREEN;
        if (durability >= 40) return Color.YELLOW;
        if (durability >= 20) return Color.ORANGE;
        return Color.RED;
    }
}