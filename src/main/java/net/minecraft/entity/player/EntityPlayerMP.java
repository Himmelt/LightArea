package net.minecraft.entity.player;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public abstract class EntityPlayerMP extends EntityPlayer {

    /* 1.7.10 & 1.12.2 - mcServer*/
    public MinecraftServer field_71133_b;

    /*
     * 1.7.10 - playerNetServerHandler
     * 1.12.2 - connection
     * */
    public NetHandlerPlayServer field_71135_a;

    public EntityPlayerMP(World world) {
        super(world);
    }

}
