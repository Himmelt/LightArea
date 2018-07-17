package cpw.mods.fml.common.network;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class FMLNetworkEvent {
    public static class ClientCustomPacketEvent {
        public FMLProxyPacket packet;
    }

    public static class ClientDisconnectionFromServerEvent {
    }
}
