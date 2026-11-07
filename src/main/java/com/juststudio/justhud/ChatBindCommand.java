package com.juststudio.justhud;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.Arrays;
import java.util.List;

public class ChatBindCommand {
    
    private static final List<String> AVAILABLE_KEYS = Arrays.asList(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "shift", "ctrl", "alt", "space", "tab", "capslock",
        "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10", "f11", "f12",
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
    );
    
    private static final SuggestionProvider<CommandSourceStack> KEY_SUGGESTIONS = 
        (context, builder) -> SharedSuggestionProvider.suggest(AVAILABLE_KEYS, builder);
    
    public void onRegisterCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("bind")
            .executes(context -> {
                context.getSource().sendSuccess(() -> 
                    Component.literal("§cUsage: .bind <key> <command>"), false);
                context.getSource().sendSuccess(() -> 
                    Component.literal("§eExample: §f.bind g spawn"), false);
                return 1;
            })
            .then(Commands.argument("key", StringArgumentType.string())
                .suggests(KEY_SUGGESTIONS)
                .then(Commands.argument("command", StringArgumentType.greedyString())
                    .executes(context -> {
                        String key = StringArgumentType.getString(context, "key").toLowerCase();
                        String command = StringArgumentType.getString(context, "command");
                        
                        if (UniversalBindSystem.createBind(key, command)) {
                            context.getSource().sendSuccess(() -> 
                                Component.literal("§aKey §e" + key + "§a bound to: §b/" + command), false);
                            context.getSource().sendSuccess(() -> 
                                Component.literal("§7Now pressing §e" + key + "§7 will execute this command"), false);
                        } else {
                            context.getSource().sendSuccess(() -> 
                                Component.literal("§cUnknown key: " + key), false);
                        }
                        return 1;
                    })
                )
            )
        );
        
        dispatcher.register(Commands.literal("unbind")
            .executes(context -> {
                context.getSource().sendSuccess(() -> 
                    Component.literal("§cUsage: .unbind <key>"), false);
                return 1;
            })
            .then(Commands.argument("key", StringArgumentType.string())
                .suggests((context, builder) -> {
                    var binds = UniversalBindSystem.getBindCommands().keySet();
                    return SharedSuggestionProvider.suggest(binds, builder);
                })
                .executes(context -> {
                    String key = StringArgumentType.getString(context, "key").toLowerCase();
                    if (UniversalBindSystem.removeBind(key)) {
                        context.getSource().sendSuccess(() -> 
                            Component.literal("§aBind for key §e" + key + "§a removed"), false);
                    } else {
                        context.getSource().sendSuccess(() -> 
                            Component.literal("§cNo bind found for key: " + key), false);
                    }
                    return 1;
                })
            )
        );
        
        dispatcher.register(Commands.literal("binds")
            .executes(context -> {
                String bindsList = UniversalBindSystem.getBindsList();
                context.getSource().sendSuccess(() -> 
                    Component.literal("§6Binds: " + bindsList), false);
                return 1;
            })
        );
    }
    
    public void onClientChat(ClientChatEvent event) {
        String message = event.getMessage();
        
        if (message.startsWith(".bind ") || message.startsWith(".unbind ") || 
            message.equals(".binds") || message.equals(".bind") || message.equals(".unbind")) {
            event.setMessage("");
            
            if (Minecraft.getInstance().player != null) {
                ClientCommandHandler.runCommand(message.substring(1));
            }
        }
    }
}