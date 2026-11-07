package com.juststudio.justhud;

import net.minecraftforge.client.event.InputEvent;

import java.util.LinkedList;
import java.util.Queue;

public class ClientEventHandler {
    private static final Queue<Long> leftClicks = new LinkedList<>();
    private static final Queue<Long> rightClicks = new LinkedList<>();

    public static void onMouseClick(InputEvent.MouseButton.Post event) {
        long now = System.currentTimeMillis();
        if (event.getButton() == 0 && event.getAction() == 1) {
            leftClicks.add(now);
        } else if (event.getButton() == 1 && event.getAction() == 1) {
            rightClicks.add(now);
        }
    }

    public static void register() {
        InputEvent.MouseButton.Post.BUS.addListener(ClientEventHandler::onMouseClick);
        net.minecraftforge.event.TickEvent.ClientTickEvent.Post.BUS.addListener(event -> {
            long now = System.currentTimeMillis();
            leftClicks.removeIf(t -> now - t > 1000);
            rightClicks.removeIf(t -> now - t > 1000);
        });
    }

    public static int getLeftCPS() { return leftClicks.size(); }
    public static int getRightCPS() { return rightClicks.size(); }
}