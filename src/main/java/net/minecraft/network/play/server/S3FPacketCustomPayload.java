package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload implements Packet<INetHandlerPlayClient> {

    public S3FPacketCustomPayload(String channel, byte[] data) {
    }

    public void readPacketData(PacketBuffer buf) {
    }

    public void writePacketData(PacketBuffer buf) {
    }

    public void processPacket(INetHandlerPlayClient handler) {
    }
}
