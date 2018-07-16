package cpw.mods.fml.common;

import cpw.mods.fml.common.eventhandler.EventBus;

public class FMLCommonHandler {

    private static FMLCommonHandler instance = new FMLCommonHandler();
    private final EventBus bus = new EventBus();

    public EventBus bus() {
        return bus;
    }

    public static FMLCommonHandler instance() {
        return null;
    }
}
