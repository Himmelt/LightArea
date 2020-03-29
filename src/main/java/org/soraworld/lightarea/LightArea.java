package org.soraworld.lightarea;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.soraworld.lightarea.proxy.CommonProxy;

/**
 * @author Himmelt
 */
@Mod(LightArea.MOD_ID)
public final class LightArea {
    public static final String MOD_ID = "lightarea";

    public LightArea() {
        CommonProxy proxy = new CommonProxy();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(proxy::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(proxy::onClientSetup);
    }
}
