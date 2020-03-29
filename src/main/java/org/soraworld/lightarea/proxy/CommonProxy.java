package org.soraworld.lightarea.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.soraworld.lightarea.handler.EventBusHandler;
import org.soraworld.lightarea.handler.FMLHandler;
import org.soraworld.lightarea.network.Area;
import org.soraworld.lightarea.network.AreaPacket;
import org.soraworld.lightarea.util.Vec3d;
import org.soraworld.lightarea.util.Vec3i;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Himmelt
 */
public class CommonProxy {

    protected Item tool = Items.field_151053_p;
    protected float speed;
    protected int AREA_ID = 0;
    public Configuration config;

    protected final HashMap<UUID, Vec3i> pos1s = new HashMap<>();
    protected final HashMap<UUID, Vec3i> pos2s = new HashMap<>();
    protected final HashMap<Integer, HashMap<Integer, Area>> lightAreas = new HashMap<>();
    protected final FMLEventChannel channel_new = NetworkRegistry.INSTANCE.newEventDrivenChannel("light");
    protected final cpw.mods.fml.common.network.FMLEventChannel channel_old = cpw.mods.fml.common.network.NetworkRegistry.INSTANCE.newEventDrivenChannel("light");

    private static final byte[] CUBOID = "s|cuboid".getBytes(StandardCharsets.UTF_8);
    private static final String WECUI_CHANNEL = "WECUI";

    private static final Object REGISTRY;
    private static final Object EVENT_BUS;
    private static final Method getObjectFromName;
    private static final Method getNameFromObject;

    public static final byte UPDATE = 1;
    public static final byte DELETE = 2;
    public static final byte GAMMA = 3;
    public static final byte SPEED = 4;
    public static final boolean v_1_7;
    public static final boolean v_1_8;
    public static final boolean v_1_9;
    public static final boolean v_1_10;
    public static final boolean v_1_11;
    public static final boolean v_1_12;
    public static final boolean v_1_13;

    static {
        boolean v1_7 = false, v1_8 = false, v1_9 = false, v1_10 = false, v1_11 = false, v1_12 = false, v1_13 = false;
        try {
            Field MC_VERSION = MinecraftForge.class.getDeclaredField("MC_VERSION");
            MC_VERSION.setAccessible(true);
            String version = (String) MC_VERSION.get(null);
            if (version.contains("1.7")) {
                v1_7 = true;
                v1_8 = v1_9 = v1_10 = v1_11 = v1_12 = v1_13 = false;
                System.out.println("MinecraftForge 1.7");
            } else if (version.contains("1.8")) {
                v1_8 = true;
                v1_7 = v1_9 = v1_10 = v1_11 = v1_12 = v1_13 = false;
                System.out.println("MinecraftForge 1.8");
            } else if (version.contains("1.9")) {
                v1_9 = true;
                v1_7 = v1_8 = v1_10 = v1_11 = v1_12 = v1_13 = false;
                System.out.println("MinecraftForge 1.9");
            } else if (version.contains("1.10")) {
                v1_10 = true;
                v1_7 = v1_8 = v1_9 = v1_11 = v1_12 = v1_13 = false;
                System.out.println("MinecraftForge 1.10");
            } else if (version.contains("1.11")) {
                v1_11 = true;
                v1_7 = v1_8 = v1_9 = v1_10 = v1_12 = v1_13 = false;
                System.out.println("MinecraftForge 1.11");
            } else if (version.contains("1.12")) {
                v1_12 = true;
                v1_7 = v1_8 = v1_9 = v1_10 = v1_11 = v1_13 = false;
                System.out.println("MinecraftForge 1.12");
            } else if (version.contains("1.13")) {
                v1_13 = true;
                v1_7 = v1_8 = v1_9 = v1_10 = v1_11 = v1_12 = false;
                System.out.println("MinecraftForge 1.13");
            }
        } catch (Throwable ignored) {
        }
        v_1_7 = v1_7;
        v_1_8 = v1_8;
        v_1_9 = v1_9;
        v_1_10 = v1_10;
        v_1_11 = v1_11;
        v_1_12 = v1_12;
        v_1_13 = v1_13;
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
            Class<?> regOld = null, regNew = null;
            try {
                regOld = Class.forName("net.minecraft.util.RegistryNamespaced");
            } catch (Throwable ignored) {
            }
            try {
                regNew = Class.forName("net.minecraft.util.registry.RegistryNamespaced");
            } catch (Throwable ignored) {
            }
            if (regOld != null) {
                //getObject = regOld.getDeclaredMethod("func_82594_a", String.class);
                //getName = regOld.getDeclaredMethod("func_148750_c", Object.class);
                Method[] methods = regOld.getDeclaredMethods();
                if (methods != null) {
                    for (Method method : methods) {
                        if (method.getName().equals("func_82594_a")) {
                            getObject = method;
                        }
                        if (method.getName().equals("func_148750_c")) {
                            getName = method;
                        }
                        if (method.getName().equals("func_177774_c")) {
                            getName = method;
                        }
                    }
                }
            } else if (regNew != null) {
                //getObject = regNew.getDeclaredMethod("func_82594_a", String.class);
                //getName = regNew.getDeclaredMethod("func_177774_c", Object.class);
                Method[] methods = regNew.getDeclaredMethods();
                if (methods != null) {
                    for (Method method : methods) {
                        if (method.getName().equals("func_82594_a")) {
                            getObject = method;
                        }
                        if (method.getName().equals("func_177774_c")) {
                            getName = method;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        getObjectFromName = getObject;
        getNameFromObject = getName;
        if (getObject == null || getName == null) {
            System.out.println("!!!!!! This shouldn't be here !");
        }
    }

    public void onPreInit(FMLPreInitializationEvent event) {
        if (v_1_8) {
            FMLCommonHandler.instance().bus().register(new FMLHandler(this));
        } else {
            regEventBus(new FMLHandler(this));
        }
    }

    public void onPreInit(cpw.mods.fml.common.event.FMLPreInitializationEvent event) {
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new FMLHandler(this));
    }

    /* 1.12.2 */
    public void onInit(FMLInitializationEvent event) {
        regEventBus(new EventBusHandler(this));
    }

    /* 1.7.10 */
    public void onInit(cpw.mods.fml.common.event.FMLInitializationEvent event) {
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
        setSelectTool(config.getString("tool", "general", "wooden_axe", "Select Tool"));
        String[] list = config.getStringList("areas", "general", new String[]{}, "Light Areas");
        lightAreas.clear();
        AREA_ID = 0;
        if (list != null && list.length > 0) {
            for (String text : list) {
                String[] ss = text.split(",");
                try {
                    int dim = Integer.parseInt(ss[0]);
                    Vec3i pos1 = new Vec3i(Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]));
                    Vec3i pos2 = new Vec3i(Integer.parseInt(ss[4]), Integer.parseInt(ss[5]), Integer.parseInt(ss[6]));
                    float gamma = ss.length >= 8 ? Float.parseFloat(ss[7]) : 1.0F;
                    float speed = ss.length >= 9 ? Float.parseFloat(ss[8]) : 0.2F;
                    addArea(dim, pos1, pos2, gamma, speed);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public void save() {
        config.get("general", "tool", "wooden_axe", "Select Tool").set(getToolName());
        List<String> list = new ArrayList<>();
        lightAreas.forEach((dim, areas) -> areas.values().forEach(area -> list.add(dim + "," + area)));
        config.get("general", "areas", new String[]{}, "Light Areas").set(list.toArray(new String[]{}));
        config.save();
    }

    private void setSelectTool(String toolName) {
        if (REGISTRY != null && getObjectFromName != null) {
            try {
                Object object = null;
                if (v_1_7) {
                    object = getObjectFromName.invoke(REGISTRY, toolName);
                } else if (v_1_8 || v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
                    object = getObjectFromName.invoke(REGISTRY, new ResourceLocation(toolName));
                }
                if (object instanceof Item) {
                    tool = (Item) object;
                } else {
                    tool = Items.field_151053_p;
                }
            } catch (Throwable ignored) {
                tool = Items.field_151053_p;
            }
        } else {
            tool = Items.field_151053_p;
        }
    }

    private String getToolName() {
        if (REGISTRY != null && getNameFromObject != null) {
            try {
                Object object = getNameFromObject.invoke(REGISTRY, tool);
                if (object instanceof String) {
                    return (String) object;
                }
                if (object instanceof ResourceLocation) {
                    return object.toString();
                }
            } catch (Throwable ignored) {
            }
        }
        return "wooden_axe";
    }

    public void setPos1(EntityPlayerMP player, Vec3i pos1, boolean msg) {
        pos1s.put(player.getUniqueID(), pos1);
        if (msg) {
            sendChatTranslation(player, "set.pos1", pos1);
        }
        updateCUI(player);
    }

    public void setPos2(EntityPlayerMP player, Vec3i pos2, boolean msg) {
        pos2s.put(player.getUniqueID(), pos2);
        if (msg) {
            sendChatTranslation(player, "set.pos2", pos2);
        }
        updateCUI(player);
    }

    public void updateCUI(EntityPlayerMP player) {
        Vec3i pos1 = pos1s.get(player.getUniqueID());
        Vec3i pos2 = pos2s.get(player.getUniqueID());
        if (pos1 == null) {
            if (pos2 == null) {
                return;
            }
            pos1 = pos2;
        } else if (pos2 == null) {
            pos2 = pos1;
        }
        int size = (pos2.x - pos1.x + 1) * (pos2.y - pos1.y + 1) * (pos2.z - pos1.z + 1);
        // CUI Packet
        if (v_1_7) {
            player.field_71135_a.sendPacket((Packet) new S3FPacketCustomPayload(WECUI_CHANNEL, CUBOID));
            player.field_71135_a.sendPacket((Packet) new S3FPacketCustomPayload(WECUI_CHANNEL, pos1.cui(1, size)));
            player.field_71135_a.sendPacket((Packet) new S3FPacketCustomPayload(WECUI_CHANNEL, pos2.cui(2, size)));
        } else if (v_1_8) {
            player.field_71135_a.sendPacket((Packet) new S3FPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(CUBOID))));
            player.field_71135_a.sendPacket((Packet) new S3FPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(pos1.cui(1, size)))));
            player.field_71135_a.sendPacket((Packet) new S3FPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(pos2.cui(2, size)))));
        } else if (v_1_9 | v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            player.field_71135_a.sendPacket((Packet) new SPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(CUBOID))));
            player.field_71135_a.sendPacket((Packet) new SPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(pos1.cui(1, size)))));
            player.field_71135_a.sendPacket((Packet) new SPacketCustomPayload(WECUI_CHANNEL, new PacketBuffer(Unpooled.copiedBuffer(pos2.cui(2, size)))));
        }
    }

    private void sendTo(ByteBuf buf, EntityPlayerMP player) {
        if (v_1_7) {
            channel_old.sendTo(new cpw.mods.fml.common.network.internal.FMLProxyPacket(buf, "light"), player);
        } else if (v_1_8 || v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            channel_new.sendTo(new FMLProxyPacket(new PacketBuffer(buf), "light"), player);
        }
    }

    private void sendToAll(ByteBuf buf) {
        if (v_1_7) {
            channel_old.sendToAll(new cpw.mods.fml.common.network.internal.FMLProxyPacket(buf, "light"));
        } else if (v_1_8 || v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            channel_new.sendToAll(new FMLProxyPacket(new PacketBuffer(buf), "light"));
        }
    }

    public void sendAllAreasTo(EntityPlayerMP player) {
        if (isDedicated(player)) {
            lightAreas.forEach((dim, areas) -> areas.forEach((id, area) -> sendUpdateTo(player, dim, id, area)));
        }
    }

    public void sendUpdateTo(EntityPlayerMP player, int dim, int id, Area area) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(UPDATE);
        AreaPacket.Update.encode(new AreaPacket.Update(dim, id, area), buf);
        sendTo(buf, player);
    }

    public void sendUpdateToAll(int dim, int id, Area area) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(UPDATE);
        AreaPacket.Update.encode(new AreaPacket.Update(dim, id, area), buf);
        sendToAll(buf);
    }

    public void sendDeleteToAll(int dim, int id) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(DELETE);
        AreaPacket.Delete.encode(new AreaPacket.Delete(dim, id), buf);
        sendToAll(buf);
    }

    public void sendGammaToAll(int dim, int id, float gamma) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(GAMMA);
        AreaPacket.Gamma.encode(new AreaPacket.Gamma(dim, id, gamma), buf);
        sendToAll(buf);
    }

    public void sendSpeedToAll(int dim, int id, float speed) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(SPEED);
        AreaPacket.Speed.encode(new AreaPacket.Speed(dim, id, speed), buf);
        sendToAll(buf);
    }

    public void createArea(EntityPlayerMP player, float light, float speed) {
        Vec3i pos1 = pos1s.get(player.getUniqueID());
        Vec3i pos2 = pos2s.get(player.getUniqueID());
        if (pos1 != null && pos2 != null) {
            Area area = addArea(player.dimension, pos1, pos2, light, speed);
            if (area == null) {
                sendChatTranslation(player, "create.conflict");
            } else {
                sendChatTranslation(player, "create.area");
                if (isDedicated(player)) {
                    sendUpdateToAll(player.dimension, AREA_ID, area);
                }
                save();
            }
        } else {
            sendChatTranslation(player, "notSelect");
        }
    }

    public Area addArea(int dim, Vec3i pos1, Vec3i pos2, float light, float speed) {
        Area area = new Area(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, light, speed);
        if (checkConflict(dim, area)) {
            return null;
        } else {
            AREA_ID++;
            lightAreas.computeIfAbsent(dim, d -> new HashMap<>()).put(AREA_ID, area);
            return area;
        }
    }

    public void deleteArea(EntityPlayerMP player) {
        Area area = findAreaAt(player);
        if (area != null) {
            lightAreas.get(player.dimension).remove(area.id);
            save();
            if (isDedicated(player)) {
                sendDeleteToAll(player.dimension, area.id);
            }
        }
    }

    public Area findAreaAt(EntityPlayer player) {
        for (Map.Entry<Integer, Area> entry : lightAreas.getOrDefault(player.dimension, new HashMap<>()).entrySet()) {
            Area area = entry.getValue();
            if (area.contains(new Vec3d(player))) {
                area.id = entry.getKey();
                return area;
            }
        }
        return null;
    }

    public boolean checkConflict(int dim, Area intent) {
        for (Area area : lightAreas.getOrDefault(dim, new HashMap<>()).values()) {
            if (intent.conflict(area)) {
                return true;
            }
        }
        return false;
    }

    public void clearSelect(EntityPlayer player) {
        pos1s.remove(player.getUniqueID());
        pos2s.remove(player.getUniqueID());
    }

    public boolean hasPerm(EntityPlayer player) {
        return player.func_70003_b(2, "gamemode");
    }

    public void sendChatTranslation(ICommandSender sender, String key, Object... args) {
        if (v_1_7 || v_1_8) {
            sender.func_145747_a(new ChatComponentTranslation(key, args));
        } else if (v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            sender.func_145747_a(new TextComponentTranslation(key, args));
        }
    }

    public void sendChatTranslation2(EntityPlayerMP player, String key, String objKey) {
        if (v_1_7 || v_1_8) {
            player.func_145747_a(new ChatComponentTranslation(key, new ChatComponentTranslation(objKey)));
        } else if (v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            player.func_145747_a(new TextComponentTranslation(key, new TextComponentTranslation(objKey)));
        }
    }

    public void sendAreaInfo(EntityPlayerMP player, int id, Area area) {
        if (v_1_7 || v_1_8) {
            ChatStyle style = new ChatStyle().func_150238_a(EnumChatFormatting.GREEN).func_150227_a(true)
                    .func_150241_a(new net.minecraft.event.ClickEvent(net.minecraft.event.ClickEvent.Action.RUN_COMMAND, "/light tp " + id));
            IChatComponent click = new ChatComponentTranslation("text.click").func_150255_a(style);
            player.func_145747_a(new ChatComponentTranslation("info.list", id, area.pos1(), area.pos2(), area.gamma, click));
        } else if (v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            Style style = new Style().setColor(TextFormatting.GREEN).setBold(true)
                    .setClickEvent(new net.minecraft.util.text.event.ClickEvent(net.minecraft.util.text.event.ClickEvent.Action.RUN_COMMAND, "/light tp " + id));
            ITextComponent click = new TextComponentTranslation("text.click").setStyle(style);
            player.func_145747_a(new TextComponentTranslation("info.list", id, area.pos1(), area.pos2(), area.gamma, click));
        }
    }

    public void commandTool(EntityPlayerMP player) {
        if (v_1_7 || v_1_8) {
            ItemStack stack = player.func_70694_bm();
            if (stack != null) {
                tool = stack.getItem();
                save();
                sendChatTranslation2(player, "tool.set", tool.getTranslationKey(stack) + ".name");
            } else {
                sendChatTranslation2(player, "tool.get", tool.getTranslationKey() + ".name");
            }
        } else if (v_1_9 || v_1_10) {
            ItemStack stack = player.func_184614_ca();
            if (stack != null) {
                tool = stack.getItem();
                save();
                sendChatTranslation2(player, "tool.set", tool.getTranslationKey(stack) + ".name");
            } else {
                sendChatTranslation2(player, "tool.get", tool.getTranslationKey() + ".name");
            }
        } else if (v_1_11 || v_1_12 || v_1_13) {
            ItemStack stack = player.func_184614_ca();
            if (stack != null && stack.getItem() != Items.AIR) {
                tool = stack.getItem();
                save();
                sendChatTranslation2(player, "tool.set", tool.getTranslationKey(stack) + ".name");
            } else {
                sendChatTranslation2(player, "tool.get", tool.getTranslationKey() + ".name");
            }
        }
    }

    public void setSpeed(float speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
        save();
    }

    public void sendSpeedToAll() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(SPEED);
        buf.writeFloat(speed);
        sendToAll(buf);
    }

    public void showList(EntityPlayerMP player, int dim, boolean all) {
        if (all) {
            lightAreas.forEach((dimId, dimAreas) -> dimAreas.forEach((id, area) -> sendAreaInfo(player, id, area)));
        } else {
            lightAreas.getOrDefault(dim, new HashMap<>()).forEach((id, area) -> sendAreaInfo(player, id, area));
        }
    }

    public void tpAreaById(EntityPlayerMP player, int id) {
        if (player == null) {
            return;
        }
        MinecraftServer server = CommonProxy.getServer(player);
        if (server == null) {
            return;
        }
        for (Map.Entry<Integer, HashMap<Integer, Area>> entry : lightAreas.entrySet()) {
            Integer key = entry.getKey();
            HashMap<Integer, Area> areas = entry.getValue();
            Area area = areas.get(id);
            if (area != null) {
                if (player.dimension != key) {
                    World world = getWorldByDim(server, key);
                    if (world != null) {
                        player.setWorld(world);
                    }
                }
                area.center(player);
                sendChatTranslation(player, "areaTpSuccess");
                return;
            }
        }
        sendChatTranslation(player, "areaIdNotFound");
    }

    public boolean isSelectTool(ItemStack stack) {
        return stack != null && stack.getItem().equals(tool);
    }

    public static World getWorldByDim(MinecraftServer server, int dim) {
        for (World world : server.worlds) {
            if (world.provider.getDimension() == dim) {
                return world;
            }
        }
        return null;
    }

    public static boolean isDedicated(EntityPlayerMP player) {
        MinecraftServer server = getServer(player);
        return server != null && server.isDedicatedServer();
    }

    public static MinecraftServer getServer(EntityPlayerMP player) {
        if (player != null && player.world instanceof WorldServer) {
            return ((WorldServer) player.world).getMinecraftServer();
        }
        return null;
    }
}
