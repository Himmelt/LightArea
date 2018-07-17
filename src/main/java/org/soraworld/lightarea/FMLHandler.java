package org.soraworld.lightarea;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class FMLHandler {

    private final CommonProxy proxy;

    public FMLHandler(CommonProxy proxy) {
        this.proxy = proxy;
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) proxy.loginSend((EntityPlayerMP) event.player);
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onLogin(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) proxy.loginSend((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        proxy.clearSelect(event.player);
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onLogout(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        proxy.clearSelect(event.player);
    }

    @SubscribeEvent
    public void onChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        proxy.clearSelect(event.player);
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onChangeDim(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        proxy.clearSelect(event.player);
    }

}
