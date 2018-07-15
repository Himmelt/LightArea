package org.soraworld.lightarea;


import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.*;

public abstract class IICommand {

    private final boolean onlyPlayer;

    final List<String> aliases = new ArrayList<>();
    private final HashMap<String, IICommand> subs = new LinkedHashMap<>();

    public IICommand(boolean onlyPlayer, String... aliases) {
        this.onlyPlayer = onlyPlayer;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public void execute(ICommandSender sender, CommandArgs args) {
        if (args.empty()) return;
        IICommand sub = subs.get(args.first());
        if (sub == null) return;
        args.next();
        if (sender instanceof EntityPlayerMP) {
            sub.execute((EntityPlayerMP) sender, args);
            return;
        }
        if (!onlyPlayer) sub.execute(sender, args);
    }

    public void execute(EntityPlayerMP player, CommandArgs args) {
        execute((ICommandSender) player, args);
    }

    public List<String> tabCompletions(CommandArgs args) {
        String first = args.first();
        if (args.size() == 1) return getMatchList(first, subs.keySet());
        if (subs.containsKey(first)) {
            args.next();
            return subs.get(first).tabCompletions(args);
        }
        return new ArrayList<>();
    }

    protected void addSub(IICommand sub) {
        for (String alias : sub.aliases) {
            subs.putIfAbsent(alias, sub);
        }
    }

    public static List<String> getMatchList(String text, Collection<String> possibles) {
        ArrayList<String> list = new ArrayList<>();
        if (text.isEmpty()) list.addAll(possibles);
        else for (String s : possibles) if (s.startsWith(text)) list.add(s);
        return list;
    }

}
