package org.soraworld.lightarea.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.soraworld.lightarea.proxy.CommonProxy;
import org.soraworld.lightarea.util.Vec3i;

/**
 * @author Himmelt
 */
public class EventBusHandler {

    private final CommonProxy proxy;

    public EventBusHandler(CommonProxy proxy) {
        this.proxy = proxy;
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent(receiveCanceled = true)
    public void onClickBlock(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = player.func_70694_bm();
        if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && proxy.isSelectTool(stack)) {
            if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                proxy.setPos1((EntityPlayerMP) player, new Vec3i(event.x, event.y, event.z), true);
                event.setCanceled(true);
            } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                proxy.setPos2((EntityPlayerMP) player, new Vec3i(event.x, event.y, event.z), true);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onLeftClick(PlayerInteractEvent event) {
        if (CommonProxy.v_1_8) {
            if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                EntityPlayer player = event.entityPlayer;
                ItemStack stack = player.func_70694_bm();
                if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && proxy.isSelectTool(stack)) {
                    proxy.setPos1((EntityPlayerMP) player, new Vec3i(event.pos.func_177958_n(), event.pos.func_177956_o(), event.pos.func_177952_p()), true);
                    event.setCanceled(true);
                }
            }
        } else if (event instanceof PlayerInteractEvent.LeftClickBlock && event.getHand() == EnumHand.MAIN_HAND) {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.func_184614_ca();
            if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && proxy.isSelectTool(stack)) {
                proxy.setPos1((EntityPlayerMP) player, new Vec3i(event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p()), true);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onRightClick(PlayerInteractEvent event) {
        if (CommonProxy.v_1_8) {
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                EntityPlayer player = event.entityPlayer;
                ItemStack stack = player.func_70694_bm();
                if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && proxy.isSelectTool(stack)) {
                    proxy.setPos2((EntityPlayerMP) player, new Vec3i(event.pos.func_177958_n(), event.pos.func_177956_o(), event.pos.func_177952_p()), true);
                    event.setCanceled(true);
                }
            }
        } else if (event instanceof PlayerInteractEvent.RightClickBlock && event.getHand() == EnumHand.MAIN_HAND) {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.func_184614_ca();
            if (player instanceof EntityPlayerMP && proxy.hasPerm(player) && proxy.isSelectTool(stack)) {
                proxy.setPos2((EntityPlayerMP) player, new Vec3i(event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p()), true);
                event.setCanceled(true);
            }
        }
    }
}
