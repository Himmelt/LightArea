package org.soraworld.lightarea;

import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventBusClientHandler {

    private final ClientProxy proxy;

    public EventBusClientHandler(ClientProxy proxy) {
        this.proxy = proxy;
    }

    @SubscribeEvent
    public void onVideoSetting_new(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (CommonProxy.v_1_8) {
            if (event.gui instanceof GuiVideoSettings) proxy.saveLight();
        } else if (event.getGui() instanceof GuiVideoSettings) {
            proxy.saveLight();
        }
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onVideoSetting_old(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiVideoSettings) {
            proxy.saveLight();
        }
    }
}
