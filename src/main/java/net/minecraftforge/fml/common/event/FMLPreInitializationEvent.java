package net.minecraftforge.fml.common.event;

import java.io.File;

/**
 * @author Himmelt
 */
public class FMLPreInitializationEvent {
    public File getSuggestedConfigurationFile() {
        return new File("invalid.conf");
    }
}
