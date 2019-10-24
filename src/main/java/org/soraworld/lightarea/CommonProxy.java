package org.soraworld.lightarea;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CommonProxy {

    protected Item tool;
    protected float speed;
    protected int AREA_ID = 0;
    protected Configuration config;

    protected final HashMap<Integer, HashSet<Area>> areas = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos1s = new HashMap<>();
    protected final HashMap<EntityPlayer, Vec3i> pos2s = new HashMap<>();
    protected final FMLEventChannel channel_new = NetworkRegistry.INSTANCE.newEventDrivenChannel("light");
    protected final cpw.mods.fml.common.network.FMLEventChannel channel_old = cpw.mods.fml.common.network.NetworkRegistry.INSTANCE.newEventDrivenChannel("light");

    private static final byte[] CUBOID = "s|cuboid".getBytes(StandardCharsets.UTF_8);
    private static final String WECUI_CHANNEL = "WECUI";

    private static final Object REGISTRY;
    private static final Object EVENT_BUS;
    private static final Method getObjectFromName;
    private static final Method getNameFromObject;

    public static final byte ADD = 1;
    public static final byte DEL = 2;
    public static final byte SPEED = 3;
    public static final byte UPDATE = 4;
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
        if (config != null) {
            config.load();
            String toolName = config.getString("tool", "general", "wooden_axe", "Select Tool");
            setSelectTool(toolName);
            speed = config.getFloat("speed", "general", 1.0F, 0.0F, 30.0F, "Light change speed (float/tick)");
            String[] strings = config.getStringList("areas", "general", new String[]{}, "Light Areas");
            areas.clear();
            if (strings != null) {
                for (String str : strings) {
                    String[] ss = str.split(",");
                    if (ss.length == 8) {
                        getDimSet(Integer.parseInt(ss[0])).add(new Area(AREA_ID++,
                                Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]),
                                Integer.parseInt(ss[4]), Integer.parseInt(ss[5]), Integer.parseInt(ss[6]),
                                Float.parseFloat(ss[7])
                        ));
                    }
                }
            }
        }
    }

    public void save() {
        if (config != null) {
            config.get("general", "tool", "wooden_axe", "Select Tool").set(getToolName());
            config.get("general", "speed", 1.0F, "Light change speed (float/tick)").set(speed);
            List<String> list = new ArrayList<>();
            areas.forEach((dim, areas) -> areas.forEach(area -> list.add("" + dim + ',' + area)));
            config.get("general", "areas", new String[]{}, "Light Areas").set(list.toArray(new String[]{}));
            config.save();
        }
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

    public HashSet<Area> getDimSet(int dim) {
        return areas.computeIfAbsent(dim, k -> new HashSet<>());
    }

    public Vec3i getPos1(EntityPlayer player) {
        if (pos1s.containsKey(player)) {
            return pos1s.get(player);
        }
        return null;
    }

    public Vec3i getPos2(EntityPlayer player) {
        if (pos2s.containsKey(player)) {
            return pos2s.get(player);
        }
        return null;
    }

    public void setPos1(EntityPlayerMP player, Vec3i pos1, boolean msg) {
        pos1s.put(player, pos1);
        if (msg) {
            sendChatTranslation(player, "set.pos1", pos1);
        }
        updateCUI(player);
    }

    public void setPos2(EntityPlayerMP player, Vec3i pos2, boolean msg) {
        pos2s.put(player, pos2);
        if (msg) {
            sendChatTranslation(player, "set.pos2", pos2);
        }
        updateCUI(player);
    }

    public void updateCUI(EntityPlayerMP player) {
        Vec3i pos1 = pos1s.get(player);
        Vec3i pos2 = pos2s.get(player);
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

    public void createArea(EntityPlayer player, float light) {
        Vec3i pos1 = pos1s.get(player);
        Vec3i pos2 = pos2s.get(player);
        //if (light < -15.0F) light = -15.0F;
        //if (light > 15.0F) light = 15.0F;
        if (pos1 != null && pos2 != null) {
            Area area = new Area(AREA_ID++, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, light);
            if (conflict(player.field_71093_bK, area)) {
                sendChatTranslation(player, "create.conflict");
            } else {
                getDimSet(player.field_71093_bK).add(area);
                sendChatTranslation(player, "create.area");
                if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).field_71133_b.isDedicatedServer()) {
                    sendAddToAll(player.field_71093_bK, area);
                }
                save();
            }
        } else {
            sendChatTranslation(player, "notSelect");
        }
    }

    public void loginSend(EntityPlayerMP player) {
        if (player.field_71133_b.isDedicatedServer()) {
            ByteBuf buf = Unpooled.buffer();
            buf.writeByte(SPEED);
            buf.writeFloat(speed);
            chSendTo(buf, player);
            areas.forEach((dim, areas) -> areas.forEach(area -> sendAdd(player, dim, area)));
        }
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

    public void sendUpdateToAll(int dim, Area area) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(UPDATE);
        buf.writeByte(dim);
        buf.writeInt(area.id);
        buf.writeFloat(area.light);
        chSendToAll(buf);
    }

    private void chSendTo(ByteBuf buf, EntityPlayerMP player) {
        if (v_1_7) {
            channel_old.sendTo(new cpw.mods.fml.common.network.internal.FMLProxyPacket(buf, "light"), player);
        } else if (v_1_8 || v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            channel_new.sendTo(new FMLProxyPacket(new PacketBuffer(buf), "light"), player);
        }
    }

    private void chSendToAll(ByteBuf buf) {
        if (v_1_7) {
            channel_old.sendToAll(new cpw.mods.fml.common.network.internal.FMLProxyPacket(buf, "light"));
        } else if (v_1_8 || v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            channel_new.sendToAll(new FMLProxyPacket(new PacketBuffer(buf), "light"));
        }
    }

    public void deleteArea(EntityPlayerMP player) {
        HashSet<Area> set = areas.get(player.field_71093_bK);
        if (set != null) {
            set.removeIf(area -> {
                if (area.contains(new Vec3d(player))) {
                    if (player.field_71133_b.isDedicatedServer()) {
                        sendDelToAll(player.field_71093_bK, area.id);
                    }
                    save();
                    return true;
                }
                return false;
            });
        }
    }

    public Area findAreaAt(EntityPlayerMP player) {
        HashSet<Area> set = getDimSet(player.field_71093_bK);
        for (Area area : set) {
            if (area.contains(new Vec3d(player))) {
                return area;
            }
        }
        return null;
    }

    public boolean conflict(int dim, Area intent) {
        HashSet<Area> set = getDimSet(dim);
        for (Area area : set) {
            if (intent.conflict(area)) {
                return true;
            }
        }
        return false;
    }

    public void clearSelect(EntityPlayer player) {
        pos1s.remove(player);
        pos2s.remove(player);
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

    public void sendAreaList(EntityPlayerMP player, Area area) {
        if (v_1_7 || v_1_8) {
            ChatStyle style = new ChatStyle().func_150238_a(EnumChatFormatting.GREEN).func_150227_a(true)
                    .func_150241_a(new net.minecraft.event.ClickEvent(net.minecraft.event.ClickEvent.Action.RUN_COMMAND, "/light tp " + area.id));
            IChatComponent click = new ChatComponentTranslation("text.click").func_150255_a(style);
            player.func_145747_a(new ChatComponentTranslation("info.list", area.id, area.toString(), click));
        } else if (v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
            Style style = new Style().setColor(TextFormatting.GREEN).setBold(true)
                    .setClickEvent(new net.minecraft.util.text.event.ClickEvent(net.minecraft.util.text.event.ClickEvent.Action.RUN_COMMAND, "/light tp " + area.id));
            ITextComponent click = new TextComponentTranslation("text.click").setStyle(style);
            player.func_145747_a(new TextComponentTranslation("info.list", area.id, area.pos1(), area.pos2(), area.light, click));
        }
    }

    public void commandTool(EntityPlayerMP player) {
        if (v_1_7 || v_1_8) {
            ItemStack stack = player.func_70694_bm();
            if (stack != null) {
                tool = stack.getItem();
                save();
                sendChatTranslation2(player, "tool.set", tool.getUnlocalizedName() + ".name");
            } else {
                sendChatTranslation2(player, "tool.get", tool.getUnlocalizedName() + ".name");
            }
        } else if (v_1_9 || v_1_10) {
            ItemStack stack = player.func_184614_ca();
            if (stack != null) {
                tool = stack.getItem();
                save();
                sendChatTranslation2(player, "tool.set", tool.getUnlocalizedName() + ".name");
            } else {
                sendChatTranslation2(player, "tool.get", tool.getUnlocalizedName() + ".name");
            }
        } else if (v_1_11 || v_1_12 || v_1_13) {
            ItemStack stack = player.func_184614_ca();
            if (stack != null && stack.getItem() != Items.AIR) {
                tool = stack.getItem();
                save();
                sendChatTranslation2(player, "tool.set", tool.getUnlocalizedName() + ".name");
            } else {
                sendChatTranslation2(player, "tool.get", tool.getUnlocalizedName() + ".name");
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
        chSendToAll(buf);
    }

    public void reset() {
        tool = Items.field_151053_p;
        speed = 1.0F;
        AREA_ID = 0;
        config = null;
        areas.clear();
        pos1s.clear();
        pos2s.clear();
    }

    public void showList(EntityPlayerMP player, int dim, boolean all) {
        if (all) {
            areas.forEach((dimId, dimAreas) -> dimAreas.forEach(area -> sendAreaList(player, area)));
        } else {
            getDimSet(dim).forEach(area -> sendAreaList(player, area));
        }
    }

    public int getDimFromName(MinecraftServer server, String worldName) {
        for (World world : server.worlds) {
            if (world.getWorldInfo().getWorldName().equals(worldName)) {
                return world.provider.getDimension();
            }
        }
        return Integer.MIN_VALUE;
    }

    public void tpToAreaById(EntityPlayerMP player, int id) {
        for (Map.Entry<Integer, HashSet<Area>> entry : areas.entrySet()) {
            for (Area area : entry.getValue()) {
                if (area.id == id) {
                    int dim = entry.getKey();
                    if (player.field_71093_bK != dim) {
                        if (v_1_7 || v_1_8) {
                            player.func_71027_c(dim);
                        } else if (v_1_9 || v_1_10 || v_1_11 || v_1_12 || v_1_13) {
                            player.func_184204_a(dim);
                        }
                    }
                    area.center(player);
                    sendChatTranslation(player, "areaTpSuccess");
                    return;
                }
            }
        }
        sendChatTranslation(player, "areaIdNotFound");
    }
}
