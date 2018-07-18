package net.minecraftforge.fml.common.network;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class FMLNetworkEvent {
    public static class ClientCustomPacketEvent {
        public FMLProxyPacket packet;

        public FMLProxyPacket getPacket() {
            return this.packet;
        }
    }

    public static class ClientDisconnectionFromServerEvent {
    }
}
