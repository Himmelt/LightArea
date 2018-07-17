package net.minecraftforge.fml.common.gameevent;

import net.minecraft.entity.player.EntityPlayer;

public class TickEvent {
    public static class PlayerTickEvent extends TickEvent {
        public EntityPlayer player;
    }
}
