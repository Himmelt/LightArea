package org.soraworld.lightarea;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashSet;

public class ClientProxy extends CommonProxy {

    private float originalLight = 0.0F;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public void onPreInit(FMLPreInitializationEvent event) {
        super.onPreInit(event);
        if (v_1_8) FMLCommonHandler.instance().bus().register(new FMLClientHandler(this));
        else regEventBus(new FMLClientHandler(this));
        channel_new.register(new FMLClientHandler(this));
        originalLight = mc.gameSettings.gammaSetting;
    }

    public void onPreInit(cpw.mods.fml.common.event.FMLPreInitializationEvent event) {
        super.onPreInit(event);
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new FMLClientHandler(this));
        channel_old.register(new FMLClientHandler(this));
        originalLight = mc.gameSettings.gammaSetting;
    }

    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        regEventBus(new EventBusClientHandler(this));
    }

    public void onInit(cpw.mods.fml.common.event.FMLInitializationEvent event) {
        super.onInit(event);
        regEventBus(new EventBusClientHandler(this));
    }

    public void setLightLevel(EntityPlayer sp) {
        if (mc.currentScreen instanceof GuiVideoSettings) return;
        float gamma = mc.gameSettings.gammaSetting;
        HashSet<Area> set = areas.get(sp.field_71093_bK);
        if (set != null) {
            Vec3d pos = new Vec3d(sp);
            for (Area area : set) {
                if (area.contains(pos)) {
                    if (gamma < area.light - speed) gamma += speed;
                    else if (gamma > area.light + speed) gamma -= speed;
                    else gamma = area.light;
                    mc.gameSettings.gammaSetting = gamma;
                    return;
                }
            }
        }
        if (originalLight > 1) originalLight = 1.0F;
        if (gamma < originalLight - speed) gamma += speed;
        else if (gamma > originalLight + speed) gamma -= speed;
        else gamma = originalLight;
        mc.gameSettings.gammaSetting = gamma;
    }

    public void saveLight() {
        originalLight = mc.gameSettings.gammaSetting;
    }

    public void reset() {
        super.reset();
        mc.gameSettings.gammaSetting = originalLight;
    }

}
