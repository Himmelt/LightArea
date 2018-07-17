package cpw.mods.fml.common.gameevent;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerEvent {
    public EntityPlayer player;

    public static class PlayerChangedDimensionEvent extends PlayerEvent {
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
    }
}
