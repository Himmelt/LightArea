package org.soraworld.lightarea;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;

public class FMLClientHandler {

    private final ClientProxy proxy;
    private static final byte ALL = 0;
    private static final byte ADD = 1;
    private static final byte DEL = 2;

    FMLClientHandler(ClientProxy proxy) {
        this.proxy = proxy;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT && event.player instanceof EntityPlayerSP) {
            EntityPlayerSP sp = (EntityPlayerSP) event.player;
            proxy.setLightLevel(sp.worldObj.provider.dimensionId, sp.posX, sp.posY, sp.posZ);
        }
    }

    @SubscribeEvent
    public void onReceivePacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        System.out.println("ClientCustomPacketEvent:" + event.side());
        ByteBuf buf = event.packet.payload();
        byte ch = buf.readByte();
        System.out.println("CH:" + ch);
        if (ch == ADD) {
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
            System.out.println("Receive Area dim:" + dim + ",area:" + id + "," + area);
            proxy.getDimSet(dim).add(area);
        } else if (ch == DEL) {
            byte dim = buf.readByte();
            int id = buf.readInt();
            proxy.getDimSet(dim).removeIf(area -> area.id == id);
        }
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        proxy.resetLight();
    }


    @SubscribeEvent
    public void onLogout(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        proxy.resetLight();
    }

}
