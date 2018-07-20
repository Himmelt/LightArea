package net.minecraftforge.fml.common.event;

import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

public class FMLServerStartingEvent {
    private MinecraftServer server;

    public void registerServerCommand(ICommand command) {
    }

    public MinecraftServer getServer() {
        return server;
    }
}
