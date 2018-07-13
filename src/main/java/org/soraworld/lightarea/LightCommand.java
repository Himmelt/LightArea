package org.soraworld.lightarea;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class LightCommand extends IICommand implements ICommand {

    public LightCommand(CommonProxy proxy, boolean onlyPlayer, String... aliases) {
        super(onlyPlayer, aliases);
        addSub(new IICommand(true, "pos1") {
            @Override
            public void execute(EntityPlayer player, CommandArgs args) {
                proxy.setPos1(player, new Vec3i(player));
                player.addChatMessage(new ChatComponentText("set Pos1:" + proxy.getPos1(player)));
            }
        });
        addSub(new IICommand(true, "pos2") {
            @Override
            public void execute(EntityPlayer player, CommandArgs args) {
                proxy.setPos2(player, new Vec3i(player));
                player.addChatMessage(new ChatComponentText("set Pos2:" + proxy.getPos2(player)));
            }
        });
        addSub(new IICommand(true, "create") {
            @Override
            public void execute(EntityPlayer player, CommandArgs args) {
                if (args.empty()) proxy.createArea(player, 0);
                else proxy.createArea(player, Float.valueOf(args.first()));
            }
        });
        addSub(new IICommand(true, "delete") {
            @Override
            public void execute(EntityPlayer player, CommandArgs args) {
                if (player instanceof EntityPlayerMP) proxy.deleteArea((EntityPlayerMP) player);
            }
        });
        addSub(new IICommand(true, "info") {
            @Override
            public void execute(EntityPlayer player, CommandArgs args) {

            }
        });
        addSub(new IICommand(true, "level") {
            @Override
            public void execute(EntityPlayer player, CommandArgs args) {

            }
        });
    }

    @Nonnull
    public String getName() {
        return aliases.get(0);
    }

    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/light pos1/pos2/create/level/info/delete";
    }

    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        execute(sender, new CommandArgs(args));
    }

    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) {
        return sender.canCommandSenderUseCommand(4, "op");
    }

    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable Vec3i targetPos) {
        return tabCompletions(new CommandArgs(args));
    }

    public String getCommandName() {
        return getName();
    }

    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    public List getCommandAliases() {
        return aliases;
    }

    public void processCommand(ICommandSender sender, String[] args) {
        execute(sender, new CommandArgs(args));
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return checkPermission(MinecraftServer.getServer(), sender);
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return tabCompletions(new CommandArgs(args));
    }

    public boolean isUsernameIndex(@Nonnull String[] args, int index) {
        return false;
    }

    public int compareTo(@Nonnull ICommand command) {
        if (command instanceof LightCommand && ((LightCommand) command).getName().equals(this.getName())) return 0;
        else return 1;
    }

    public int compareTo(Object object) {
        if (object instanceof LightCommand && ((LightCommand) object).getName().equals(this.getName())) return 0;
        else return 1;
    }
}
