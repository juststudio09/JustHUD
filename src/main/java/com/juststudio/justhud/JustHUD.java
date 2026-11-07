package com.juststudio.justhud;

import net.minecraftforge.api.distmarker.Dist;
import com.juststudio.justhud.gui.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(JustHUD.MOD_ID)
public class JustHUD {
    public static final String MOD_ID = "justhud";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static RenderOverlay HUD_RENDERER;
    private static EntityInfoRenderer ENTITY_RENDERER;

    public JustHUD() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC, "justhud.toml");

        if (FMLEnvironment.dist == Dist.CLIENT) {
            HUD_RENDERER = new RenderOverlay();
            ENTITY_RENDERER = new EntityInfoRenderer();

            ClientEventHandler.register();
            KeystrokesRenderer.register();
            WorldJoinHandler.register();
            RegisterKeyMappingsEvent.BUS.addListener(UniversalBindSystem::registerKeyMappings);
            ChatBindCommand chatCommands = new ChatBindCommand();
            ClientChatEvent.BUS.addListener(chatCommands::onClientChat);
            RegisterClientCommandsEvent.BUS.addListener(chatCommands::onRegisterCommands);
            ScreenEvent.Init.Post.BUS.addListener(JustHUD::onScreenInit);

            AddGuiOverlayLayersEvent.BUS.addListener(event -> event.getLayeredDraw().add(
                    Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
                    (graphics, deltaTracker) -> {
                        HUD_RENDERER.render(graphics);
                        ENTITY_RENDERER.render(graphics);
                        KeystrokesRenderer.render(graphics);
                    }
            ));
        }
    }

    private static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof OptionsScreen screen) {
            Button button = Button.builder(Component.translatable("justhud.menu.title"), btn ->
                    Minecraft.getInstance().setScreen(new ConfigScreen(screen)))
                    .bounds(screen.width / 2 - 75, screen.height / 6 + 144, 150, 20)
                    .build();
            event.addListener(button);
        }
    }
}
