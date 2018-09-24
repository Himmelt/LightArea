package org.soraworld.lightarea;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class LightCommand extends IICommand implements ICommand {

    public LightCommand(CommonProxy proxy, boolean onlyPlayer, String... aliases) {
        super(onlyPlayer, aliases);
        addSub(new IICommand(true, "pos1") {
            @Override
            public void execute(EntityPlayerMP player, CommandArgs args) {
                proxy.setPos1(player, new Vec3i(player), true);
            }
        });
        addSub(new IICommand(true, "pos2") {
            @Override
            public void execute(EntityPlayerMP player, CommandArgs args) {
                proxy.setPos2(player, new Vec3i(player), true);
            }
        });
        addSub(new IICommand(true, "create") {
            @Override
            public void execute(EntityPlayerMP player, CommandArgs args) {
                if (args.empty()) proxy.createArea(player, 0);
                else try {
                    proxy.createArea(player, Float.valueOf(args.first()));
                } catch (Throwable e) {
                    proxy.sendChatTranslation(player, "invalid.float");
                }
            }
        });
        addSub(new IICommand(true, "delete") {
            @Override
            public void execute(EntityPlayerMP player, CommandArgs args) {
                proxy.deleteArea(player);
            }
        });
        addSub(new IICommand(true, "info") {
            @Override
            public void execute(EntityPlayerMP player, CommandArgs args) {
                Area area = proxy.findAreaAt(player);
                if (area != null) {
                    proxy.sendChatTranslation(player, "info.pos1", area.pos1());
                    proxy.sendChatTranslation(player, "info.pos2", area.pos2());
                    proxy.sendChatTranslation(player, "info.light", area.light);
                    proxy.setPos1(player, area.vec1(), false);
                    proxy.setPos2(player, area.vec2(), false);
                } else {
                    proxy.sendChatTranslation(player, "info.notInArea");
                }
            }
        });
        addSub(new IICommand(true, "list") {
            public void execute(EntityPlayerMP player, CommandArgs args) {
                if (args.empty()) {
                    proxy.showList(player, player.field_71093_bK, false);
                    return;
                }
                if (args.first().equals("all")) {
                    proxy.showList(player, 0, true);
                    return;
                }
                try {
                    proxy.showList(player, Integer.valueOf(args.first()), false);
                } catch (Throwable ignored) {
                    proxy.showList(player, proxy.getDimFromName(player.field_71133_b, args.first()), false);
                }
            }
        });
        addSub(new IICommand(true, "tp") {
            public void execute(EntityPlayerMP player, CommandArgs args) {
                if (args.notEmpty()) {
                    try {
                        proxy.tpToAreaById(player, Integer.valueOf(args.first()));
                    } catch (Throwable e) {
                        proxy.sendChatTranslation(player, "invalid.int");
                    }
                } else proxy.sendChatTranslation(player, "empty.args");
            }
        });
        addSub(new IICommand(true, "level") {
            @Override
            public void execute(EntityPlayerMP player, CommandArgs args) {
                Area area = proxy.findAreaAt(player);
                if (area != null) {
                    if (args.notEmpty()) {
                        try {
                            float old = area.light;
                            area.light = Float.valueOf(args.first());
                            //if (area.light < -15.0F) area.light = -15.0F;
                            //if (area.light > 15.0F) area.light = 15.0F;
                            if (old != area.light) {
                                if (player.field_71133_b.isDedicatedServer()) {
                                    proxy.sendUpdateToAll(player.dimension, area);
                                }
                                proxy.save();
                            }
                            proxy.sendChatTranslation(player, "info.light", area.light);
                        } catch (Throwable e) {
                            proxy.sendChatTranslation(player, "invalid.float");
                        }
                    } else proxy.sendChatTranslation(player, "info.light", area.light);
                } else proxy.sendChatTranslation(player, "info.notInArea");
            }
        });
        addSub(new IICommand(true, "tool") {
            @Override
            public void execute(EntityPlayerMP player, CommandArgs args) {
                proxy.commandTool(player);
            }
        });
        addSub(new IICommand(true, "speed") {
            public void execute(EntityPlayerMP player, CommandArgs args) {
                if (args.notEmpty()) {
                    try {
                        proxy.setSpeed(Float.valueOf(args.first()));
                        proxy.sendChatTranslation(player, "speed.set", proxy.speed);
                        if (player.field_71133_b.isDedicatedServer()) {
                            proxy.sendSpeedToAll();
                        }
                    } catch (Throwable ignored) {
                        proxy.sendChatTranslation(player, "invalid.float");
                    }
                } else proxy.sendChatTranslation(player, "speed.get", proxy.speed);
            }
        });
    }

    public String func_71517_b() {
        return aliases.get(0);
    }

    public String func_71518_a(ICommandSender sender) {
        return "/light pos1/pos2/create/level/info/delete/tool";
    }

    public List func_71514_a() {
        return aliases;
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) {
        execute(sender, new CommandArgs(args));
    }

    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return sender.func_70003_b(4, "op");
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return tabCompletions(new CommandArgs(args));
    }

    public boolean func_82358_a(String[] args, int index) {
        return false;
    }

    public List func_180525_a(ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos) {
        return tabCompletions(new CommandArgs(args));
    }

    public int compareTo(ICommand command) {
        if (command instanceof LightCommand && command.func_71517_b().equals(this.func_71517_b())) return 0;
        else return 1;
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
        execute(sender, new CommandArgs(args));
    }

    /* 1.7.10 - canCommandSenderUseCommand */
    public boolean func_71519_b(ICommandSender sender) {
        return sender.func_70003_b(4, "op");
    }

    /* 1.7.10 - addTabCompletionOptions */
    public List func_71516_a(ICommandSender sender, String[] args) {
        return tabCompletions(new CommandArgs(args));
    }

/*
    public int compareTo(Object object) {
        if (object instanceof LightCommand && ((LightCommand) object).getName().equals(this.getName())) return 0;
        else return 1;
    }
*/

}
