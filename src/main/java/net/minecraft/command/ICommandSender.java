package net.minecraft.command;

public interface ICommandSender {

    /*
     * 1.7.10 - canCommandSenderUseCommand
     * 1.12.2 - canUseCommand
     * */
    boolean func_70003_b(int lvl, String cmd);

}
