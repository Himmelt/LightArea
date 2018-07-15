package org.soraworld.lightarea;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;

public class ClientProxy extends CommonProxy {

    private float originalLight = 0.0F;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public void onPreInit(FMLPreInitializationEvent event) {
        super.onPreInit(event);
        FMLCommonHandler.instance().bus().register(new FMLClientHandler(this));
        channel.register(new FMLClientHandler(this));
        originalLight = mc.gameSettings.gammaSetting;
    }

    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        MinecraftForge.EVENT_BUS.register(new EventBusClientHandler(this));
    }

    public void setLightLevel(EntityPlayer sp) {
        if (mc.currentScreen instanceof GuiVideoSettings) return;
        HashSet<Area> set = areas.get(sp.dimension);
        if (set != null) {
            for (Area area : set) {
                if (area.contains(new Vec3d(sp))) {
                    mc.gameSettings.gammaSetting = area.light;
                    return;
                }
            }
        }
        mc.gameSettings.gammaSetting = originalLight;
    }

    public void resetLight(boolean changeOptions) {
        if (changeOptions) originalLight = mc.gameSettings.gammaSetting;
        else mc.gameSettings.gammaSetting = originalLight;
    }

}
