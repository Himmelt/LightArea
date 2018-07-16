package cpw.mods.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FMLProxyPacket {
    public FMLProxyPacket(ByteBuf buf, String channel) {
    }

    public ByteBuf payload() {
        return Unpooled.buffer();
    }
}
