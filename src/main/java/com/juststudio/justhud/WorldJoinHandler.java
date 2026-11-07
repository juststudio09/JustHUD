package com.juststudio.justhud;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class WorldJoinHandler {
    private static boolean wasInWorld = false;
    private static int joinTimer = 0;
    
    public static void register() {
        net.minecraftforge.event.TickEvent.ClientTickEvent.Post.BUS.addListener(event -> {
            Minecraft mc = Minecraft.getInstance();
            boolean isInWorld = mc.player != null && mc.level != null;

            if (isInWorld && !wasInWorld) joinTimer = 40;
            if (joinTimer > 0 && --joinTimer == 0) showWelcomeMessage();
            wasInWorld = isInWorld;
        });
    }
    
    private static void showWelcomeMessage() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("§6=== JustProject Mod v2.0 ==="), false);
            mc.player.displayClientMessage(Component.literal("§aFPS Display: §fFPS, Coordinates, CPS, Armor"), false);
            mc.player.displayClientMessage(Component.literal("§aEntity Info: §fReal HP when looking at mobs"), false);
            mc.player.displayClientMessage(Component.literal("§aBind System: §fUse .bind to create key binds"), false);
        }
    }
}