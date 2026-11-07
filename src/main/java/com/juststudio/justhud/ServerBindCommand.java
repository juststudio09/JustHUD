package com.juststudio.justhud;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

/**
 * Informational server-side bind commands. Actual key binds are client-side.
 */
public class ServerBindCommand {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("sbind")
            .executes(context -> {
                context.getSource().sendSuccess(() -> Component.literal("Usage: /sbind <key> \"<command>\""), false);
                context.getSource().sendSuccess(() -> Component.literal("Example: /sbind g \"spawn\""), false);
                context.getSource().sendSuccess(() -> Component.literal("Note: Binds only work in singleplayer"), false);
                return 1;
            })
            .then(Commands.argument("key", StringArgumentType.string())
                .then(Commands.argument("command", StringArgumentType.greedyString())
                    .executes(context -> {
                        String key = StringArgumentType.getString(context, "key").toLowerCase();
                        String command = StringArgumentType.getString(context, "command");
                        if (command.startsWith("\"") && command.endsWith("\"") && command.length() >= 2) {
                            command = command.substring(1, command.length() - 1);
                        }
                        final String finalKey = key;
                        final String finalCommand = command;
                        context.getSource().sendSuccess(() -> Component.literal("Server bind created for key: " + finalKey), true);
                        context.getSource().sendSuccess(() -> Component.literal("Command: /" + finalCommand), false);
                        context.getSource().sendSuccess(() -> Component.literal("Note: Key binds only work in singleplayer!"), false);
                        return 1;
                    })
                )
            )
        );

        dispatcher.register(Commands.literal("sbinds")
            .executes(context -> {
                context.getSource().sendSuccess(() -> Component.literal("Server Bind Info"), false);
                context.getSource().sendSuccess(() -> Component.literal("Key binds are client-side only"), false);
                context.getSource().sendSuccess(() -> Component.literal("They work only in singleplayer"), false);
                context.getSource().sendSuccess(() -> Component.literal("Use /sbind to create server-side bind records"), false);
                return 1;
            })
        );
    }
}
