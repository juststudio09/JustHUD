package com.juststudio.justhud.gui;

import com.juststudio.justhud.UniversalBindSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class BindSettingsScreen extends Screen {
    private final Screen parentScreen;
    private List<BindEntry> bindEntries = new ArrayList<>();
    private int scrollOffset = 0;
    private BindEntry listeningEntry = null;
    private static final int MAX_VISIBLE_ENTRIES = 6;
    private static final int BUTTON_WIDTH = 80;
    private static final int FIELD_WIDTH = 120;
    private static final int ENTRY_HEIGHT = 25;

    public BindSettingsScreen(Screen parentScreen) {
        super(Component.literal("Настройки биндов"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        loadBinds();
        initGui();
    }

    private void loadBinds() {
        bindEntries.clear();
        var binds = UniversalBindSystem.getBindCommands();
        binds.forEach((key, cmd) -> bindEntries.add(new BindEntry(key, cmd)));
    }

    private void initGui() {
        this.clearWidgets();
        
        int centerX = this.width / 2;
        int startY = 60;

        // Add bind button
        this.addRenderableWidget(Button.builder(
                Component.literal("+ Добавить бинд"),
                btn -> addNewBind()
            ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 50, BUTTON_WIDTH, 20)
            .build()
        );

        // Назад button
        this.addRenderableWidget(Button.builder(
                Component.literal("Назад"),
                btn -> this.onClose()
            ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 25, BUTTON_WIDTH, 20)
            .build()
        );

        // Bind entries
        for (int i = scrollOffset; i < Math.min(bindEntries.size(), scrollOffset + MAX_VISIBLE_ENTRIES); i++) {
            BindEntry entry = bindEntries.get(i);
            int yPos = startY + (i - scrollOffset) * ENTRY_HEIGHT;

            // Клавиша button
            Button keyButton = Button.builder(
                    Component.literal("> " + entry.key + " <"),
                    btn -> startListening(entry)
                ).bounds(centerX - 150, yPos, BUTTON_WIDTH, 20)
                .build();
            this.addRenderableWidget(keyButton);
            entry.keyButton = keyButton;

            // Команда field
            EditBox commandField = new EditBox(
                this.font,
                centerX - 60, yPos,
                FIELD_WIDTH, 20,
                Component.literal("Команда")
            );
            commandField.setValue(entry.command);
            commandField.setResponder(text -> {
                entry.command = text;
                UniversalBindSystem.createBind(entry.key, entry.command);
            });
            this.addRenderableWidget(commandField);
            entry.commandField = commandField;

            // Удалить button
            this.addRenderableWidget(Button.builder(
                    Component.literal("Удалить"),
                    btn -> deleteBind(entry)
                ).bounds(centerX + 70, yPos, 60, 20)
                .build()
            );
        }

        // Scroll buttons
        if (bindEntries.size() > MAX_VISIBLE_ENTRIES) {
            this.addRenderableWidget(Button.builder(
                    Component.literal("↑"),
                    btn -> scrollUp()
                ).bounds(this.width - 30, 50, 20, 20)
                .build()
            );
            this.addRenderableWidget(Button.builder(
                    Component.literal("↓"),
                    btn -> scrollDown()
                ).bounds(this.width - 30, this.height - 80, 20, 20)
                .build()
            );
        }
    }

    private void startListening(BindEntry entry) {
        listeningEntry = entry;
        entry.keyButton.setMessage(Component.literal("> Нажмите клавишу <"));
    }

    private void stopListening() {
        if (listeningEntry != null) {
            listeningEntry.keyButton.setMessage(Component.literal("> " + listeningEntry.key + " <"));
            listeningEntry = null;
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (listeningEntry != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                stopListening();
                return true;
            }
            
            String keyName = getKeyName(keyCode);
            if (keyName != null) {
                if (!listeningEntry.key.equals(keyName)) {
                    UniversalBindSystem.removeBind(listeningEntry.key);
                }
                listeningEntry.key = keyName;
                UniversalBindSystem.createBind(keyName, listeningEntry.command);
                stopListening();
                initGui();
                return true;
            }
        }
        return super.keyPressed(event);
    }

    private String getKeyName(int keyCode) {
        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
            return String.valueOf((char) ('a' + (keyCode - GLFW.GLFW_KEY_A)));
        }
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            return String.valueOf((char) ('0' + (keyCode - GLFW.GLFW_KEY_0)));
        }
        
        return switch (keyCode) {
            case GLFW.GLFW_KEY_SPACE -> "space";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT -> "alt";
            case GLFW.GLFW_KEY_TAB -> "tab";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "capslock";
            case GLFW.GLFW_KEY_F1 -> "f1"; case GLFW.GLFW_KEY_F2 -> "f2";
            case GLFW.GLFW_KEY_F3 -> "f3"; case GLFW.GLFW_KEY_F4 -> "f4";
            case GLFW.GLFW_KEY_F5 -> "f5"; case GLFW.GLFW_KEY_F6 -> "f6";
            case GLFW.GLFW_KEY_F7 -> "f7"; case GLFW.GLFW_KEY_F8 -> "f8";
            case GLFW.GLFW_KEY_F9 -> "f9"; case GLFW.GLFW_KEY_F10 -> "f10";
            case GLFW.GLFW_KEY_F11 -> "f11"; case GLFW.GLFW_KEY_F12 -> "f12";
            default -> null;
        };
    }

    private void addNewBind() {
        BindEntry entry = new BindEntry("f", "say Привет");
        bindEntries.add(entry);
        UniversalBindSystem.createBind(entry.key, entry.command);
        initGui();
    }

    private void deleteBind(BindEntry entry) {
        UniversalBindSystem.removeBind(entry.key);
        bindEntries.remove(entry);
        initGui();
    }

    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
            initGui();
        }
    }

    private void scrollDown() {
        if (scrollOffset < bindEntries.size() - MAX_VISIBLE_ENTRIES) {
            scrollOffset++;
            initGui();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        
        graphics.drawCenteredString(this.font, 
            Component.literal("Настройки биндов"), 
            this.width / 2, 20, 0xFFFFFF);
        
        if (listeningEntry != null) {
            graphics.drawCenteredString(this.font, 
                Component.literal("§aНажмите клавишу... (ESC to cancel)"), 
                this.width / 2, 40, 0x00FF00);
        }
        
        int centerX = this.width / 2;
        graphics.drawString(this.font, "Клавиша", centerX - 150, 45, 0xFFFFFF);
        graphics.drawString(this.font, "Команда", centerX - 60, 45, 0xFFFFFF);
        graphics.drawString(this.font, "Действие", centerX + 70, 45, 0xFFFFFF);
        
        graphics.drawString(this.font, 
            Component.literal("§7Биндов: " + bindEntries.size()), 
            this.width - 80, 25, 0x808080);
        
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        stopListening();
        Minecraft.getInstance().setScreen(parentScreen);
    }

    private static class BindEntry {
        String key;
        String command;
        Button keyButton;
        EditBox commandField;
        
        BindEntry(String key, String command) {
            this.key = key;
            this.command = command;
        }
    }
}