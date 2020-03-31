package org.soraworld.lightarea.handler;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.soraworld.lightarea.proxy.CommonProxy;

/**
 * @author Himmelt
 */
public class ClientEventHandler {

    private final CommonProxy proxy;

    public ClientEventHandler(CommonProxy proxy) {
        this.proxy = proxy;
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof VideoSettingsScreen) {
            proxy.saveLight();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ClientPlayerEntity) {
            proxy.updateClientGamma(event.player);
        }
    }

    @SubscribeEvent
    public void onLogout(GuiOpenEvent event) {
        Screen screen = event.getGui();
        if (screen instanceof MainMenuScreen || screen instanceof MultiplayerScreen) {
            proxy.clientReset();
        }
    }
}
