package org.soraworld.lightarea;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CommonProxy {

    protected int AREA_ID = 0;
    protected Configuration config;
    protected final HashMap<Integer, HashSet<Area>> areas = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos1s = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos2s = new HashMap<>();
    protected final FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("light");

    private static final byte ALL = 0;
    private static final byte ADD = 1;
    private static final byte DEL = 2;

    public void onPreInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), "1.0.0");
        FMLCommonHandler.instance().bus().register(new FMLHandler(this));
    }

    public void onInit(FMLInitializationEvent event) {
        config.load();
        String[] strings = config.getStringList("areas", "general", new String[]{}, "Light Areas");
        if (strings != null) {
            for (String str : strings) {
                String[] ss = str.split(",");
                if (ss.length == 8) {
                    getDimSet(Integer.valueOf(ss[0])).add(new Area(AREA_ID++,
                            Integer.valueOf(ss[1]), Integer.valueOf(ss[2]), Integer.valueOf(ss[3]),
                            Integer.valueOf(ss[4]), Integer.valueOf(ss[5]), Integer.valueOf(ss[6]),
                            Float.valueOf(ss[7])
                    ));
                }
            }
        }
    }

    public HashSet<Area> getDimSet(int dim) {
        return areas.computeIfAbsent(dim, k -> new HashSet<>());
    }

    public Vec3i getPos1(EntityPlayer player) {
        if (pos1s.containsKey(player)) return pos1s.get(player);
        return null;
    }

    public Vec3i getPos2(EntityPlayer player) {
        if (pos2s.containsKey(player)) return pos2s.get(player);
        return null;
    }

    public void setPos1(EntityPlayer player, Vec3i pos1) {
        pos1s.put(player, pos1);
    }

    public void setPos2(EntityPlayer player, Vec3i pos2) {
        pos2s.put(player, pos2);
    }

    public void createArea(EntityPlayer player, float light) {
        Vec3i pos1 = pos1s.get(player);
        Vec3i pos2 = pos2s.get(player);
        player.addChatMessage(new ChatComponentText("create light:" + light));
        player.addChatMessage(new ChatComponentText("pos1:" + pos1 + ";pos2" + pos2));
        if (pos1 != null && pos2 != null) {
            player.addChatMessage(new ChatComponentText("dim:" + player.worldObj.provider.dimensionId));
            Area area = new Area(AREA_ID++, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, light);
            getDimSet(player.worldObj.provider.dimensionId).add(area);
            for (Object mp : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                if (mp instanceof EntityPlayerMP) {
                    sendAddArea((EntityPlayerMP) mp, player.worldObj.provider.dimensionId, area);
                }
            }
            save();
        }
    }

    public void save() {
        List<String> list = new ArrayList<>();
        areas.forEach((dim, areas) -> areas.forEach(area -> list.add("" + dim + ',' + area)));
        config.get("general", "areas", new String[]{}).set(list.toArray(new String[]{}));
        config.save();
    }

    public void sendAll(EntityPlayerMP player) {
        areas.forEach((dim, areas) -> areas.forEach(area -> sendAddArea(player, dim, area)));
    }

    public void sendAddArea(EntityPlayerMP player, int dim, Area area) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(ADD);
        buf.writeByte(dim);
        buf.writeInt(area.id);
        buf.writeInt(area.x1);
        buf.writeInt(area.y1);
        buf.writeInt(area.z1);
        buf.writeInt(area.x2);
        buf.writeInt(area.y2);
        buf.writeInt(area.z2);
        buf.writeFloat(area.light);
        channel.sendTo(new FMLProxyPacket(buf, "light"), player);
    }

    public void sendDelArea(EntityPlayerMP player, int dim, int id) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(DEL);
        buf.writeByte(dim);
        buf.writeInt(id);
        channel.sendTo(new FMLProxyPacket(buf, "light"), player);
    }

    public void deleteArea(EntityPlayerMP player) {
        HashSet<Area> set = areas.get(player.worldObj.provider.dimensionId);
        if (set != null) set.removeIf(area -> {
            if (area.contains(player.posX, player.posY, player.posZ)) {
                sendDelArea(player, player.worldObj.provider.dimensionId, area.id);
                return true;
            }
            return false;
        });
    }

}
