package org.soraworld.lightarea;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventBusHandler {

    private final CommonProxy proxy;

    public EventBusHandler(CommonProxy proxy) {
        this.proxy = proxy;
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent(receiveCanceled = true)
    public void onClickBlock(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = player.func_70694_bm();
        if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && stack != null && stack.getItem().equals(proxy.tool)) {
            if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                proxy.setPos1((EntityPlayerMP) player, new Vec3i(event.x, event.y, event.z), true);
                event.setCanceled(true);
            } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                proxy.setPos2((EntityPlayerMP) player, new Vec3i(event.x, event.y, event.z), true);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLeftClick(PlayerInteractEvent event) {
        if (event instanceof PlayerInteractEvent.LeftClickBlock) {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.func_70694_bm();
            if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && stack != null && stack.getItem().equals(proxy.tool)) {
                proxy.setPos1((EntityPlayerMP) player, new Vec3i(event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p()), true);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        if (event instanceof PlayerInteractEvent.RightClickBlock) {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.func_70694_bm();
            if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && stack != null && stack.getItem().equals(proxy.tool)) {
                proxy.setPos2((EntityPlayerMP) player, new Vec3i(event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p()), true);
                event.setCanceled(true);
            }
        }
    }

}
