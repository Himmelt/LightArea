package net.minecraftforge.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

public class FMLProxyPacket {
    public FMLProxyPacket(PacketBuffer buffer, String channel) {
    }

    public ByteBuf payload() {
        return Unpooled.buffer();
    }
}
