package org.soraworld.lightarea;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;

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
    }

    public void setLightLevel(int dim, double x, double y, double z) {
        HashSet<Area> set = areas.get(dim);
        if (set != null) {
            for (Area area : set) {
                if (area.contains(x, y, z)) {
                    mc.gameSettings.gammaSetting = area.light;
                    return;
                }
            }
        }
        mc.gameSettings.gammaSetting = originalLight;
    }

    public void resetLight() {
        mc.gameSettings.gammaSetting = originalLight;
    }

}
