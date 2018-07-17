package org.soraworld.lightarea;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CommonProxy {

    protected int AREA_ID = 0;
    protected Configuration config;
    protected Item tool;
    protected final HashMap<Integer, HashSet<Area>> areas = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos1s = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos2s = new HashMap<>();
    protected final FMLEventChannel channel_new = NetworkRegistry.INSTANCE.newEventDrivenChannel("light");
    protected final cpw.mods.fml.common.network.FMLEventChannel channel_old = cpw.mods.fml.common.network.NetworkRegistry.INSTANCE.newEventDrivenChannel("light");

    private static final byte ALL = 0;
    private static final byte ADD = 1;
    private static final byte DEL = 2;
    private static final byte UPDATE = 3;
    private static final byte[] CUBOID = "s|cuboid".getBytes(StandardCharsets.UTF_8);
    private static final String WECUI_CHANNEL = "WECUI";

    private static final Object REGISTRY;
    private static final Object EVENT_BUS;
    private static final Method getObjectFromName;
    private static final Method getNameFromObject;

    public static final boolean v_1_7;
    public static final boolean v_1_12;

    static {
        boolean v17 = false, v112 = false;
        try {
            Field MC_VERSION = MinecraftForge.class.getDeclaredField("MC_VERSION");
            MC_VERSION.setAccessible(true);
            String version = (String) MC_VERSION.get(null);
            if (version.contains("1.7")) {
                v17 = true;
                v112 = false;
            } else if (version.contains("1.12")) {
                v17 = false;
                v112 = true;
            }
        } catch (Throwable ignored) {
            v17 = false;
            v112 = false;
        }
        v_1_7 = v17;
        v_1_12 = v112;
        Field field;
        Object obj = null;
        try {
            field = Item.class.getDeclaredField("field_150901_e");
            field.setAccessible(true);
            obj = field.get(null);
        } catch (Throwable ignored) {
        }
        REGISTRY = obj;
        obj = null;
        try {
            field = MinecraftForge.class.getDeclaredField("EVENT_BUS");
            field.setAccessible(true);
            obj = field.get(null);
        } catch (Throwable ignored) {
        }
        EVENT_BUS = obj;
        Method getObject = null, getName = null;
        try {
            Class<?> reg_1_7 = Class.forName("net.minecraft.util.RegistryNamespaced");
            Class<?> reg_1_12 = Class.forName("net.minecraft.util.registry.RegistryNamespaced");
            if (reg_1_7 != null) {
                getObject = reg_1_7.getDeclaredMethod("func_82594_a", String.class);
                getName = reg_1_7.getDeclaredMethod("func_148750_c", Object.class);
            } else if (reg_1_12 != null) {
                getObject = reg_1_12.getDeclaredMethod("func_82594_a", String.class);
                getName = reg_1_12.getDeclaredMethod("func_177774_c", Object.class);
            }
        } catch (Throwable ignored) {
        }
        getObjectFromName = getObject;
        getNameFromObject = getName;
    }

    public void onPreInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), LightArea.MOD_VERSION);
        regEventBus(new FMLHandler(this));
    }

    public void onPreInit(cpw.mods.fml.common.event.FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), LightArea.MOD_VERSION);
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new FMLHandler(this));
    }

    /* 1.12.2 */
    public void onInit(FMLInitializationEvent event) {
        load();
        regEventBus(new EventBusHandler(this));
    }

    /* 1.7.10 */
    public void onInit(cpw.mods.fml.common.event.FMLInitializationEvent event) {
        load();
        regEventBus(new EventBusHandler(this));
    }

    public void regEventBus(Object object) {
        if (EVENT_BUS instanceof EventBus) {
            ((EventBus) EVENT_BUS).register(object);
        } else if (EVENT_BUS instanceof cpw.mods.fml.common.eventhandler.EventBus) {
            ((cpw.mods.fml.common.eventhandler.EventBus) EVENT_BUS).register(object);
        }
    }

    public void load() {
        config.load();
        String toolName = config.getString("tool", "general", "wooden_axe", "Select Tool");
        setSelectTool(toolName);
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

    private void setSelectTool(String toolName) {
        if (REGISTRY != null && getObjectFromName != null) {
            try {
                Object object = getObjectFromName.invoke(REGISTRY, toolName);
                if (object instanceof Item) tool = (Item) object;
                else tool = Items.field_151053_p;
            } catch (Throwable ignored) {
                tool = Items.field_151053_p;
            }
        } else tool = Items.field_151053_p;
    }

    private String getToolName() {
        if (REGISTRY != null && getNameFromObject != null) {
            try {
                Object object = getNameFromObject.invoke(REGISTRY, tool);
                if (object instanceof String) return (String) object;
            } catch (Throwable ignored) {
            }
        }
        return "wooden_axe";
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
        if (msg) sendChatTranslation(player, "set.pos1", pos1);
        updateCUI(player);
    }

    public void setPos2(EntityPlayerMP player, Vec3i pos2, boolean msg) {
        pos2s.put(player, pos2);
        if (msg) sendChatTranslation(player, "set.pos2", pos2);
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
        if (v_1_7) {
            player.field_71135_a.sendPacket((Packet<?>) new S3FPacketCustomPayload(WECUI_CHANNEL, CUBOID));
            player.field_71135_a.sendPacket((Packet<?>) new S3FPacketCustomPayload(WECUI_CHANNEL, pos1.cui(1, size)));
            player.field_71135_a.sendPacket((Packet<?>) new S3FPacketCustomPayload(WECUI_CHANNEL, pos2.cui(2, size)));
        } else if (v_1_12) {
            player.field_71135_a.sendPacket((Packet<?>) new SPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(CUBOID))));
            player.field_71135_a.sendPacket((Packet<?>) new SPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(pos1.cui(1, size)))));
            player.field_71135_a.sendPacket((Packet<?>) new SPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(pos2.cui(2, size)))));
        }
    }

    public void createArea(EntityPlayer player, float light) {
        Vec3i pos1 = pos1s.get(player);
        Vec3i pos2 = pos2s.get(player);
        if (light < -15.0F) light = -15.0F;
        if (light > 15.0F) light = 15.0F;
        if (pos1 != null && pos2 != null) {
            Area area = new Area(AREA_ID++, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, light);
            getDimSet(player.field_71093_bK).add(area);
            sendChatTranslation(player, "create.area");
            sendAddToAll(player.field_71093_bK, area);
            save();
        }
    }

    public void save() {
        config.get("general", "tool", "wooden_axe", "Select Tool").set(getToolName());
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
        chSendTo(buf, player);
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
        chSendToAll(buf);
    }

    public void sendDelToAll(int dim, int id) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(DEL);
        buf.writeByte(dim);
        buf.writeInt(id);
        chSendToAll(buf);
    }

    private void chSendTo(ByteBuf buf, EntityPlayerMP player) {
        if (v_1_7) {
            channel_old.sendTo(new cpw.mods.fml.common.network.internal.FMLProxyPacket(buf, "light"), player);
        } else if (v_1_12) {
            channel_new.sendTo(new FMLProxyPacket(new PacketBuffer(buf), "light"), player);
        }
    }

    private void chSendToAll(ByteBuf buf) {
        if (v_1_7) {
            channel_old.sendToAll(new cpw.mods.fml.common.network.internal.FMLProxyPacket(buf, "light"));
        } else if (v_1_12) {
            channel_new.sendToAll(new FMLProxyPacket(new PacketBuffer(buf), "light"));
        }
    }

    public void sendUpdateToAll(int dim, Area area) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(UPDATE);
        buf.writeByte(dim);
        buf.writeInt(area.id);
        buf.writeFloat(area.light);
        chSendToAll(buf);
    }

    public void deleteArea(EntityPlayerMP player) {
        HashSet<Area> set = areas.get(player.field_71093_bK);
        if (set != null) set.removeIf(area -> {
            if (area.contains(new Vec3d(player))) {
                if (player.field_71133_b.isDedicatedServer()) sendDelToAll(player.field_71093_bK, area.id);
                save();
                return true;
            }
            return false;
        });
    }

    public Area findAreaAt(EntityPlayerMP player) {
        HashSet<Area> set = getDimSet(player.field_71093_bK);
        for (Area area : set) if (area.contains(new Vec3d(player))) return area;
        return null;
    }

    public void clearSelect(EntityPlayer player) {
        pos1s.remove(player);
        pos2s.remove(player);
    }

    public boolean hasPerm(EntityPlayer player) {
        return player.func_70003_b(4, "op");
    }

    public void sendChatTranslation(EntityPlayer player, String key, Object... args) {
        if (v_1_7) {
            player.func_145747_a(new ChatComponentTranslation(key, args));
        } else if (v_1_12) {
            player.func_145747_a(new TextComponentTranslation(key, args));
        }
    }

    public void sendChatTranslation2(EntityPlayerMP player, String key, String objKey) {
        if (v_1_7) {
            player.func_145747_a(new ChatComponentTranslation(key, new ChatComponentTranslation(objKey)));
        } else if (v_1_12) {
            player.func_145747_a(new TextComponentTranslation(key, new TextComponentTranslation(objKey)));
        }
    }

}
