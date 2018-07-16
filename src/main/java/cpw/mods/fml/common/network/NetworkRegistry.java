package cpw.mods.fml.common.network;

public enum NetworkRegistry {
    INSTANCE;

    public FMLEventChannel newEventDrivenChannel(String name) {
        return new FMLEventChannel(name);
    }
}
