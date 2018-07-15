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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CommonProxy {

    protected int AREA_ID = 0;
    protected Configuration config;
    protected Item tool = Items.wooden_axe;
    protected final HashMap<Integer, HashSet<Area>> areas = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos1s = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos2s = new HashMap<>();
    protected final FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("light");

    private static final byte ALL = 0;
    private static final byte ADD = 1;
    private static final byte DEL = 2;
    private static final byte UPDATE = 3;
    private static final byte[] CUBOID = "s|cuboid".getBytes(StandardCharsets.UTF_8);
    private static final String WECUI_CHANNEL = "WECUI";

    public void onPreInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), "1.0.0");
        FMLCommonHandler.instance().bus().register(new FMLHandler(this));
    }

    public void onInit(FMLInitializationEvent event) {
        load();
        MinecraftForge.EVENT_BUS.register(new EventBusHandler(this));
    }

    public void load() {
        config.load();
        String toolName = config.getString("tool", "general", "wooden_axe", "Select Tool");
        Object object = Item.itemRegistry.getObject(toolName);
        if (object instanceof Item) tool = (Item) object;
        else tool = Items.wooden_axe;
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

    public void setPos1(EntityPlayerMP player, Vec3i pos1, boolean msg) {
        pos1s.put(player, pos1);
        if (msg) player.addChatMessage(new ChatComponentTranslation("set.pos1", pos1));
        updateCUI(player);
    }

    public void setPos2(EntityPlayerMP player, Vec3i pos2, boolean msg) {
        pos2s.put(player, pos2);
        if (msg) player.addChatMessage(new ChatComponentTranslation("set.pos2", pos2));
        updateCUI(player);
    }

    public void updateCUI(EntityPlayerMP player) {
        Vec3i pos1 = pos1s.get(player);
        Vec3i pos2 = pos2s.get(player);
        if (pos1 == null) {
            if (pos2 == null) return;
            pos1 = pos2;
        } else if (pos2 == null) pos2 = pos1;
        int size = (pos2.x - pos1.x + 1) * (pos2.y - pos1.y + 1) * (pos2.z - pos1.z + 1);
        // CUI Packet
        player.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload(WECUI_CHANNEL, CUBOID));
        player.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload(WECUI_CHANNEL, pos1.cui(1, size)));
        player.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload(WECUI_CHANNEL, pos2.cui(2, size)));
    }

    public void createArea(EntityPlayer player, float light) {
        Vec3i pos1 = pos1s.get(player);
        Vec3i pos2 = pos2s.get(player);
        if (light < -15.0F) light = -15.0F;
        if (light > 15.0F) light = 15.0F;
        if (pos1 != null && pos2 != null) {
            Area area = new Area(AREA_ID++, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, light);
            getDimSet(player.dimension).add(area);
            player.addChatMessage(new ChatComponentTranslation("create.area"));
            sendAddToAll(player.dimension, area);
            save();
        }
    }

    public void save() {
        config.get("general", "tool", "wooden_axe", "Select Tool").set(Item.itemRegistry.getNameForObject(tool));
        List<String> list = new ArrayList<>();
        areas.forEach((dim, areas) -> areas.forEach(area -> list.add("" + dim + ',' + area)));
        config.get("general", "areas", new String[]{}, "Light Areas").set(list.toArray(new String[]{}));
        config.save();
    }

    public void loginSend(EntityPlayerMP player) {
        areas.forEach((dim, areas) -> areas.forEach(area -> sendAdd(player, dim, area)));
    }

    public void sendAdd(EntityPlayerMP player, int dim, Area area) {
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

    public void sendAddToAll(int dim, Area area) {
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
        channel.sendToAll(new FMLProxyPacket(buf, "light"));
    }

    public void sendDelToAll(int dim, int id) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(DEL);
        buf.writeByte(dim);
        buf.writeInt(id);
        channel.sendToAll(new FMLProxyPacket(buf, "light"));
    }

    public void sendUpdateToAll(int dim, Area area) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(UPDATE);
        buf.writeByte(dim);
        buf.writeInt(area.id);
        buf.writeFloat(area.light);
        channel.sendToAll(new FMLProxyPacket(buf, "light"));
    }

    public void deleteArea(EntityPlayerMP player) {
        HashSet<Area> set = areas.get(player.dimension);
        if (set != null) set.removeIf(area -> {
            if (area.contains(new Vec3d(player))) {
                sendDelToAll(player.dimension, area.id);
                save();
                return true;
            }
            return false;
        });
    }

    public Area findAreaAt(EntityPlayer player) {
        HashSet<Area> set = getDimSet(player.dimension);
        for (Area area : set) if (area.contains(new Vec3d(player))) return area;
        return null;
    }

    public void clearSelect(EntityPlayer player) {
        pos1s.remove(player);
        pos2s.remove(player);
    }

    public boolean hasPerm(EntityPlayer player) {
        return player.canCommandSenderUseCommand(4, "op");
    }

}
