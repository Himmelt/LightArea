package org.soraworld.lightarea.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.soraworld.lightarea.network.Area;
import org.soraworld.lightarea.proxy.CommonProxy;

/**
 * @author Himmelt
 */
public class LightCommand extends ICommand {

    private static LightCommand command;

    public static void register(CommandDispatcher<CommandSource> dispatcher, CommonProxy proxy) {
        if (command == null) {
            command = new LightCommand(proxy, true, "light");
        }
        dispatcher.register(Commands.literal("light")
                .requires((source) -> source.hasPermissionLevel(2))
                .then(Commands.argument("args", StringArgumentType.greedyString())
                        .executes(context -> {
                            Entity entity = context.getSource().getEntity();
                            if (entity instanceof EntityPlayerMP) {
                                command.execute(entity, new Args(StringArgumentType.getString(context, "args")));
                            }
                            return 1;
                        })
                )
        );
    }

    public LightCommand(CommonProxy proxy, boolean onlyPlayer, String... aliases) {
        super(onlyPlayer, aliases);
        addSub(new ICommand(true, "pos1") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                proxy.setPos1(player, player.getPosition(), true);
            }
        });
        addSub(new ICommand(true, "pos2") {
            @Override
            public void execute(EntityPlayerMP player, Args args) {
                proxy.setPos2(player, player.getPosition(), true);
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
                    proxy.showList(player, player.dimension.getId(), false);
                    return;
                }
                if ("all".equals(args.first())) {
                    proxy.showList(player, 0, true);
                    return;
                }
                try {
                    proxy.showList(player, Integer.parseInt(args.first()), false);
                } catch (Throwable ignored) {
                    MinecraftServer server = CommonProxy.getServer(player);
                    if (server != null) {
                        proxy.showList(player, proxy.getDimFromName(server, args.first()), false);
                    }
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
                                    proxy.sendGammaToAll(player.dimension.getId(), area.id, area.gamma);
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
                                    proxy.sendSpeedToAll(player.dimension.getId(), area.id, area.speed);
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
}
