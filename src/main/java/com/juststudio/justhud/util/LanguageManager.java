package com.juststudio.justhud.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class LanguageManager {
    
    public static String getString(String key) {
        return Component.translatable(key).getString();
    }
    
    public static String getString(String key, Object... args) {
        return Component.translatable(key, args).getString();
    }
    
    public static Component getText(String key) {
        return Component.translatable(key);
    }
    
    public static Component getText(String key, Object... args) {
        return Component.translatable(key, args);
    }
}