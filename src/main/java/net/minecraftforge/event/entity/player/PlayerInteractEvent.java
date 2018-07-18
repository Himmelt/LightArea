package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PlayerInteractEvent extends Event {

    /* 1.7.10*/
    public enum Action {
        RIGHT_CLICK_AIR,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_BLOCK
    }

    /* 1.7.10-1.8 */
    public Action action;
    /* 1.7.10 */
    public int x, y, z;
    /* 1.8 */
    public net.minecraft.util.BlockPos pos;
    /* 1.7.10-1.8 */
    public EntityPlayer entityPlayer;

    /* 1.12.2 */
    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    /* 1.12.2 */
    public EnumHand getHand() {
        return EnumHand.MAIN_HAND;
    }

    /* 1.12.2 */
    public BlockPos getPos() {
        return null;
    }

    /* 1.12.2 */
    public static class LeftClickBlock extends PlayerInteractEvent {
    }

    /* 1.12.2 */
    public static class RightClickBlock extends PlayerInteractEvent {
    }

}
