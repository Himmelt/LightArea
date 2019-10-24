package org.soraworld.lightarea;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * @author Himmelt
 */
public class FMLClientHandler {

    private final ClientProxy proxy;

    FMLClientHandler(ClientProxy proxy) {
        this.proxy = proxy;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof EntityPlayerSP) {
            proxy.setLightLevel(event.player);
        }
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onPlayerTick(cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent event) {
        if (event.player instanceof EntityPlayerSP) {
            proxy.setLightLevel(event.player);
        }
    }

    @SubscribeEvent
    public void onReceivePacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        if (CommonProxy.v_1_8) handlePacket(event.packet.payload());
        else handlePacket(event.getPacket().payload());
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onReceivePacket(cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent event) {
        handlePacket(event.packet.payload());
    }

    private void handlePacket(ByteBuf buf) {
        byte ch = buf.readByte();
        if (ch == CommonProxy.ADD) {
            byte dim = buf.readByte();
            int id = buf.readInt();
            int x1 = buf.readInt();
            int y1 = buf.readInt();
            int z1 = buf.readInt();
            int x2 = buf.readInt();
            int y2 = buf.readInt();
            int z2 = buf.readInt();
            float light = buf.readFloat();
            Area area = new Area(id, x1, y1, z1, x2, y2, z2, light);
            proxy.getDimSet(dim).add(area);
        } else if (ch == CommonProxy.DEL) {
            byte dim = buf.readByte();
            int id = buf.readInt();
            proxy.getDimSet(dim).removeIf(area -> area.id == id);
        } else if (ch == CommonProxy.SPEED) {
            proxy.setSpeed(buf.readFloat());
        } else if (ch == CommonProxy.UPDATE) {
            byte dim = buf.readByte();
            int id = buf.readInt();
            float light = buf.readFloat();
            proxy.getDimSet(dim).forEach(area -> {
                if (area.id == id) area.light = light;
            });
        }
    }

/*
    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        proxy.reset();
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onLogout(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        proxy.reset();
    }
*/

    @SubscribeEvent
    public void onLogout(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        proxy.reset();
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onLogout(cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        proxy.reset();
    }
}
