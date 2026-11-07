package com.juststudio.justhud;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UniversalBindSystem {
    private static final Map<String, KeyMapping> KEY_BINDS = new HashMap<>();
    private static final Map<String, String> BIND_COMMANDS = new HashMap<>();
    private static final File BINDS_FILE = new File("config/justproject_binds.txt");
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("justhud", "binds"));

    static { loadBinds(); }

    public static boolean createBind(String keyName, String command) {
        Integer keyCode = getKeyCode(keyName);
        if (keyCode == null) return false;

        if (KEY_BINDS.containsKey(keyName)) removeBind(keyName);

        KeyMapping keyBind = new KeyMapping(
                "justproject.bind." + keyName,
                InputConstants.Type.KEYSYM,
                keyCode,
                CATEGORY
        );

        ClientRegistry.registerKeyBinding(keyBind);
        KEY_BINDS.put(keyName, keyBind);
        BIND_COMMANDS.put(keyName, command);
        saveBinds();
        return true;
    }

    public static boolean removeBind(String keyName) {
        if (KEY_BINDS.containsKey(keyName)) {
            KEY_BINDS.remove(keyName);
            BIND_COMMANDS.remove(keyName);
            saveBinds();
            return true;
        }
        return false;
    }

    public static Map<String, String> getBindCommands() {
        return new HashMap<>(BIND_COMMANDS);
    }

    public static String getBindsList() {
        if (BIND_COMMANDS.isEmpty()) return "No active binds";
        StringBuilder sb = new StringBuilder("Active binds:\n");
        BIND_COMMANDS.forEach((key, cmd) -> sb.append("  ").append(key).append(" -> /").append(cmd).append("\n"));
        return sb.toString();
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ClientRegistry.onRegisterKeyMappings(event);
        InputEvent.Key.BUS.addListener(new KeyHandler()::onKeyInput);
    }

    public static class KeyHandler {
        public void onKeyInput(InputEvent.Key event) {
            if (event.getAction() != GLFW.GLFW_PRESS) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null || mc.player == null) return;

            for (var entry : KEY_BINDS.entrySet()) {
                if (entry.getValue().matches(event.getInfo())) {
                    String cmd = BIND_COMMANDS.get(entry.getKey());
                    if (cmd != null) mc.player.connection.sendCommand(cmd);
                    break;
                }
            }
        }
    }

    private static void saveBinds() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BINDS_FILE))) {
            BIND_COMMANDS.forEach((k, v) -> writer.println(k + "=" + v));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadBinds() {
        if (!BINDS_FILE.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(BINDS_FILE))) {
            reader.lines().map(l -> l.split("=", 2)).filter(p -> p.length == 2)
                    .forEach(p -> createBind(p[0], p[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Integer getKeyCode(String name) {
        return switch (name) {
            case "a" -> GLFW.GLFW_KEY_A; case "b" -> GLFW.GLFW_KEY_B; case "c" -> GLFW.GLFW_KEY_C;
            case "d" -> GLFW.GLFW_KEY_D; case "e" -> GLFW.GLFW_KEY_E; case "f" -> GLFW.GLFW_KEY_F;
            case "g" -> GLFW.GLFW_KEY_G; case "h" -> GLFW.GLFW_KEY_H; case "i" -> GLFW.GLFW_KEY_I;
            case "j" -> GLFW.GLFW_KEY_J; case "k" -> GLFW.GLFW_KEY_K; case "l" -> GLFW.GLFW_KEY_L;
            case "m" -> GLFW.GLFW_KEY_M; case "n" -> GLFW.GLFW_KEY_N; case "o" -> GLFW.GLFW_KEY_O;
            case "p" -> GLFW.GLFW_KEY_P; case "q" -> GLFW.GLFW_KEY_Q; case "r" -> GLFW.GLFW_KEY_R;
            case "s" -> GLFW.GLFW_KEY_S; case "t" -> GLFW.GLFW_KEY_T; case "u" -> GLFW.GLFW_KEY_U;
            case "v" -> GLFW.GLFW_KEY_V; case "w" -> GLFW.GLFW_KEY_W; case "x" -> GLFW.GLFW_KEY_X;
            case "y" -> GLFW.GLFW_KEY_Y; case "z" -> GLFW.GLFW_KEY_Z;
            case "shift" -> GLFW.GLFW_KEY_LEFT_SHIFT; case "ctrl" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "alt" -> GLFW.GLFW_KEY_LEFT_ALT; case "space" -> GLFW.GLFW_KEY_SPACE;
            case "tab" -> GLFW.GLFW_KEY_TAB; case "capslock" -> GLFW.GLFW_KEY_CAPS_LOCK;
            case "f1" -> GLFW.GLFW_KEY_F1; case "f2" -> GLFW.GLFW_KEY_F2; case "f3" -> GLFW.GLFW_KEY_F3;
            case "f4" -> GLFW.GLFW_KEY_F4; case "f5" -> GLFW.GLFW_KEY_F5; case "f6" -> GLFW.GLFW_KEY_F6;
            case "f7" -> GLFW.GLFW_KEY_F7; case "f8" -> GLFW.GLFW_KEY_F8; case "f9" -> GLFW.GLFW_KEY_F9;
            case "f10" -> GLFW.GLFW_KEY_F10; case "f11" -> GLFW.GLFW_KEY_F11; case "f12" -> GLFW.GLFW_KEY_F12;
            case "1" -> GLFW.GLFW_KEY_1; case "2" -> GLFW.GLFW_KEY_2; case "3" -> GLFW.GLFW_KEY_3;
            case "4" -> GLFW.GLFW_KEY_4; case "5" -> GLFW.GLFW_KEY_5; case "6" -> GLFW.GLFW_KEY_6;
            case "7" -> GLFW.GLFW_KEY_7; case "8" -> GLFW.GLFW_KEY_8; case "9" -> GLFW.GLFW_KEY_9;
            case "0" -> GLFW.GLFW_KEY_0;
            default -> null;
        };
    }
}