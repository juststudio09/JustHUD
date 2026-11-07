package com.juststudio.justhud;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public final class ClientRegistry {
    private static final List<KeyMapping> PENDING = new ArrayList<>();

    private ClientRegistry() {}

    public static void registerKeyBinding(KeyMapping mapping) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options != null) {
            if (!ArrayUtils.contains(minecraft.options.keyMappings, mapping)) {
                minecraft.options.keyMappings = ArrayUtils.add(minecraft.options.keyMappings, mapping);
                KeyMapping.resetMapping();
            }
        } else {
            PENDING.add(mapping);
        }
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        PENDING.forEach(event::register);
        PENDING.clear();
    }
}
