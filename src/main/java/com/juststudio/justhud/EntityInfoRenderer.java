package com.juststudio.justhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.awt.*;

public class EntityInfoRenderer {
    private final Minecraft mc = Minecraft.getInstance();

    public void render(GuiGraphics currentGraphics) {
        if (!Config.SHOW_ENTITY_INFO.get()) return;
        
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) mc.hitResult;
            if (entityHit.getEntity() instanceof LivingEntity entity && !(entity instanceof Player)) {
                renderEntityInfo(currentGraphics, entity);
            }
        }
    }
    
    private void renderEntityInfo(GuiGraphics graphics, LivingEntity entity) {
        Font font = mc.font;
        
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float absorption = entity.getAbsorptionAmount();
        
        String healthText;
        if (absorption > 0) {
            healthText = String.format("%.1f / %.1f (+%.1f)", health, maxHealth, absorption);
        } else {
            healthText = String.format("%.1f / %.1f", health, maxHealth);
        }
        
        String nameText = entity.getName().getString();
        
        int healthWidth = font.width(healthText);
        int nameWidth = font.width(nameText);
        int maxWidth = Math.max(healthWidth, nameWidth);
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        int x = Config.ENTITY_INFO_X.get() == 0 ? 
            screenWidth / 2 - maxWidth / 2 : 
            (Config.ENTITY_INFO_X.get() > 0 ? Config.ENTITY_INFO_X.get() : screenWidth + Config.ENTITY_INFO_X.get());
        
        int y = Config.ENTITY_INFO_Y.get() == 0 ? 
            screenHeight / 2 + 20 : 
            (Config.ENTITY_INFO_Y.get() > 0 ? Config.ENTITY_INFO_Y.get() : screenHeight + Config.ENTITY_INFO_Y.get());
        
        int padding = 4;
        int backgroundWidth = maxWidth + padding * 2;
        
        graphics.fill(x - padding, y - padding, x + maxWidth + padding, y + 20, 0x80000000);
        graphics.drawString(font, nameText, x, y, Color.WHITE.getRGB());
        graphics.drawString(font, healthText, x, y + 10, getHealthColor(health, maxHealth).getRGB());
    }
    
    private Color getHealthColor(float health, float maxHealth) {
        float percent = health / maxHealth;
        if (percent >= 0.7f) return Color.GREEN;
        if (percent >= 0.4f) return Color.YELLOW;
        if (percent >= 0.2f) return new Color(255, 165, 0);
        return Color.RED;
    }
}