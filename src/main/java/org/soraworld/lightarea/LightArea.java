package org.soraworld.lightarea;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.File;

@cpw.mods.fml.common.Mod(
        modid = LightArea.MOD_ID,
        name = LightArea.MOD_NAME,
        version = LightArea.MOD_VERSION,
        acceptedMinecraftVersions = "[1.7.10,1.12.2]"
)
@Mod(
        modid = LightArea.MOD_ID,
        name = LightArea.MOD_NAME,
        version = LightArea.MOD_VERSION,
        acceptedMinecraftVersions = "[1.7.10,1.12.2]"
)
public class LightArea {

    public static final String MOD_ID = "lightarea";
    public static final String MOD_NAME = "LightArea";
    public static final String MOD_VERSION = "1.0.6";

    @cpw.mods.fml.common.SidedProxy(
            clientSide = "org.soraworld.lightarea.ClientProxy",
            serverSide = "org.soraworld.lightarea.CommonProxy"
    )
    @SidedProxy(
            clientSide = "org.soraworld.lightarea.ClientProxy",
            serverSide = "org.soraworld.lightarea.CommonProxy"
    )
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.onPreInit(event);
    }

    @cpw.mods.fml.common.Mod.EventHandler
    public void onPreInit(cpw.mods.fml.common.event.FMLPreInitializationEvent event) {
        proxy.onPreInit(event);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @cpw.mods.fml.common.Mod.EventHandler
    public void onInit(cpw.mods.fml.common.event.FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new LightCommand(proxy, true, "light"));
        if (event.getServer().getEntityWorld() instanceof WorldServer) {
            WorldServer world = (WorldServer) event.getServer().getEntityWorld();
            File conf = new File(world.getChunkSaveLocation(), MOD_ID + ".cfg");
            System.out.println("FMLServerStartingEvent:" + world);
            proxy.config = new Configuration(conf, MOD_VERSION);
            proxy.load();
        }
    }

    @cpw.mods.fml.common.Mod.EventHandler
    public void onServerStarting(cpw.mods.fml.common.event.FMLServerStartingEvent event) {
        event.registerServerCommand(new LightCommand(proxy, true, "light"));
        if (event.getServer().getEntityWorld() instanceof WorldServer) {
            WorldServer world = (WorldServer) event.getServer().getEntityWorld();
            File conf = new File(world.getChunkSaveLocation(), MOD_ID + ".cfg");
            System.out.println("FMLServerStartingEvent:" + world);
            proxy.config = new Configuration(conf, MOD_VERSION);
            proxy.load();
        }
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        System.out.println("FMLServerStoppingEvent:" + event);
        proxy.reset();
    }

    @cpw.mods.fml.common.Mod.EventHandler
    public void onServerStopping(cpw.mods.fml.common.event.FMLServerStoppingEvent event) {
        System.out.println("FMLServerStoppingEvent:" + event);
        proxy.reset();
    }

}
