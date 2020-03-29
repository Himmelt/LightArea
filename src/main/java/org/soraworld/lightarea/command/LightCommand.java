package org.soraworld.lightarea.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.soraworld.lightarea.network.Area;
import org.soraworld.lightarea.proxy.CommonProxy;
import org.soraworld.lightarea.util.Vec3i;

import java.util.List;

/**
 * @author Himmelt
 */
public class LightCommand extends ICommand implements net.minecraft.command.ICommand {

    public LightCommand(CommonProxy proxy, boolean onlyPlayer, String... aliases) {
        super(onlyPlayer, aliases);
        addSub(new ICommand(true, "pos1") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                proxy.setPos1(player, new Vec3i(player), true);
            }
        });
        addSub(new ICommand(true, "pos2") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                proxy.setPos2(player, new Vec3i(player), true);
            }
        });
        addSub(new ICommand(true, "create") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                try {
                    float gamma = args.size() >= 1 ? Float.parseFloat(args.get(0)) : 1.0F;
                    float speed = args.size() >= 2 ? Float.parseFloat(args.get(1)) : 0.2F;
                    proxy.createArea(player, gamma, speed);
                } catch (Throwable e) {
                    proxy.sendChatTranslation(player, "invalid.float");
                }
            }
        });
        addSub(new ICommand(true, "delete") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                proxy.deleteArea(player);
            }
        });
        addSub(new ICommand(true, "info") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                Area area = proxy.findAreaAt(player);
                if (area != null) {
                    proxy.sendChatTranslation(player, "info.pos1", area.pos1());
                    proxy.sendChatTranslation(player, "info.pos2", area.pos2());
                    proxy.sendChatTranslation(player, "info.light", area.gamma);
                    proxy.sendChatTranslation(player, "info.speed", area.speed);
                    proxy.setPos1(player, area.vec1(), false);
                    proxy.setPos2(player, area.vec2(), false);
                } else {
                    proxy.sendChatTranslation(player, "info.notInArea");
                }
            }
        });
        addSub(new ICommand(true, "list") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                if (args.empty()) {
                    proxy.showList(player, player.dimension, false);
                    return;
                }
                if ("all".equals(args.first())) {
                    proxy.showList(player, 0, true);
                    return;
                }
                try {
                    proxy.showList(player, Integer.parseInt(args.first()), false);
                } catch (Throwable ignored) {
                    proxy.sendChatTranslation(player, "invalid.int");
                }
            }
        });
        addSub(new ICommand(true, "tp") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                if (args.notEmpty()) {
                    try {
                        proxy.tpAreaById(player, Integer.parseInt(args.first()));
                    } catch (Throwable e) {
                        proxy.sendChatTranslation(player, "invalid.int");
                    }
                } else {
                    proxy.sendChatTranslation(player, "empty.args");
                }
            }
        });
        addSub(new ICommand(true, "level") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                Area area = proxy.findAreaAt(player);
                if (area != null) {
                    if (args.notEmpty()) {
                        try {
                            float old = area.gamma;
                            area.gamma = Float.parseFloat(args.first());
                            if (old != area.gamma) {
                                if (CommonProxy.isDedicated(player)) {
                                    proxy.sendGammaToAll(player.dimension, area.id, area.gamma);
                                }
                                proxy.save();
                            }
                            proxy.sendChatTranslation(player, "info.light", area.gamma);
                        } catch (Throwable e) {
                            proxy.sendChatTranslation(player, "invalid.float");
                        }
                    } else {
                        proxy.sendChatTranslation(player, "info.light", area.gamma);
                    }
                } else {
                    proxy.sendChatTranslation(player, "info.notInArea");
                }
            }
        });
        addSub(new ICommand(true, "speed") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                Area area = proxy.findAreaAt(player);
                if (area != null) {
                    if (args.notEmpty()) {
                        try {
                            float old = area.speed;
                            area.speed = Float.parseFloat(args.first());
                            if (old != area.speed) {
                                if (CommonProxy.isDedicated(player)) {
                                    proxy.sendSpeedToAll(player.dimension, area.id, area.speed);
                                }
                                proxy.save();
                            }
                            proxy.sendChatTranslation(player, "info.speed", area.speed);
                        } catch (Throwable e) {
                            proxy.sendChatTranslation(player, "invalid.float");
                        }
                    } else {
                        proxy.sendChatTranslation(player, "info.speed", area.speed);
                    }
                } else {
                    proxy.sendChatTranslation(player, "info.notInArea");
                }
            }
        });
        addSub(new ICommand(true, "tool") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                proxy.commandTool(player);
            }
        });
    }

    public String func_71517_b() {
        return aliases.get(0);
    }

    public String func_71518_a(ICommandSender sender) {
        return "/light pos1/pos2/create/level/speed/info/delete/tool";
    }

    public List func_71514_a() {
        return aliases;
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) {
        execute(sender, new Args(args));
    }

    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return sender.func_70003_b(2, "gamemode");
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return tabCompletions(new Args(args));
    }

    public boolean func_82358_a(String[] args, int index) {
        return false;
    }

    public List func_180525_a(ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos) {
        return tabCompletions(new Args(args));
    }

    public int compareTo(net.minecraft.command.ICommand command) {
        if (command instanceof LightCommand && command.func_71517_b().equals(this.func_71517_b())) {
            return 0;
        } else {
            return 1;
        }
    }

    /* 1.7.10 & 1.10.2 - getCommandName */
/*
    public String func_71517_b() {
        return getName();
    }
*/

    /* 1.7.10 - getCommandUsage */
/*
    public String func_71518_a(ICommandSender sender) {
        return getUsage(sender);
    }
*/

    /* 1.7.10 - getCommandAliases */
/*
    public List func_71514_a() {
        return aliases;
    }
*/

    /* 1.7.10 - processCommand */
    public void func_71515_b(ICommandSender sender, String[] args) {
        execute(sender, new Args(args));
    }

    /* 1.7.10 - canCommandSenderUseCommand */
    public boolean func_71519_b(ICommandSender sender) {
        return sender.func_70003_b(4, "op");
    }

    /* 1.7.10 - addTabCompletionOptions */
    public List func_71516_a(ICommandSender sender, String[] args) {
        return tabCompletions(new Args(args));
    }

/*
    public int compareTo(Object object) {
        if (object instanceof LightCommand && ((LightCommand) object).getName().equals(this.getName())) return 0;
        else return 1;
    }
*/
}
