package org.soraworld.lightarea;

import net.minecraft.entity.player.EntityPlayerMP;

public class FMLHandler {

    private final CommonProxy proxy;

    public FMLHandler(CommonProxy proxy) {
        this.proxy = proxy;
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onLogin(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) proxy.loginSend((EntityPlayerMP) event.player);
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onLogout(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        proxy.clearSelect(event.player);
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onChangeDim(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        proxy.clearSelect(event.player);
    }

}
