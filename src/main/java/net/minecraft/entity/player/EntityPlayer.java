package net.minecraft.entity.player;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public abstract class EntityPlayer extends Entity implements ICommandSender {

    /*
     * 1.7.10 - dimension
     * 1.12.2 - dimension
     * */
    public int field_71093_bK;

    public EntityPlayer(World world) {
        super(world);
    }

    /*
     * 1.12.2 - sendMessage
     * */
    public abstract void func_145747_a(ITextComponent component);

    /*
     * 1.7.10 - addChatMessage
     * */
    public abstract void func_145747_a(IChatComponent p_145747_1_);

    /*
     * 1.7.10 - getHeldItem
     * */
    public abstract ItemStack func_70694_bm();

    /*
     * 1.12.2 - getHeldItemMainhand */
    public abstract ItemStack func_184614_ca();

}
