package org.soraworld.lightarea;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraftforge.client.event.GuiScreenEvent;

public class EventBusClientHandler {

    private final ClientProxy proxy;

    public EventBusClientHandler(ClientProxy proxy) {
        this.proxy = proxy;
    }

    @SubscribeEvent
    public void onVideoSetting(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiVideoSettings) {
            proxy.resetLight(true);
        }
    }

}
