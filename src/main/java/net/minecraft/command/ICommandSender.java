package net.minecraft.command;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.text.ITextComponent;

public interface ICommandSender {

    /*
     * 1.7.10 - canCommandSenderUseCommand
     * 1.12.2 - canUseCommand
     * */
    boolean func_70003_b(int lvl, String cmd);

    /*
     * 1.12.2 - sendMessage
     * */
    void func_145747_a(ITextComponent component);

    /*
     * 1.7.10 - addChatMessage
     * */
    void func_145747_a(IChatComponent p_145747_1_);

}
