package net.minecraftforge.fml.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class FMLEventChannel {
    FMLEventChannel(String name) {
    }

    public void register(Object object) {
    }

    public void sendToAll(FMLProxyPacket pkt) {
    }

    public void sendTo(FMLProxyPacket pkt, EntityPlayerMP player) {
    }
}
