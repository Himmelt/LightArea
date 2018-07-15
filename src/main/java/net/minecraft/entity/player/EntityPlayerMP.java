package net.minecraft.entity.player;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public abstract class EntityPlayerMP extends EntityPlayer {

    /*
     * 1.7.10 - playerNetServerHandler
     * 1.12.2 - connection
     * */
    public NetHandlerPlayServer field_71135_a;

    public EntityPlayerMP(World world) {
        super(world);
    }

}
