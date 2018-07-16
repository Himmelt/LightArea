package cpw.mods.fml.common.network;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import net.minecraft.entity.player.EntityPlayerMP;

public class FMLEventChannel {
    public FMLEventChannel(String name) {
    }

    public void register(Object object) {
    }

    public void sendToAll(FMLProxyPacket pkt) {
    }

    public void sendTo(FMLProxyPacket pkt, EntityPlayerMP player) {
    }
}
