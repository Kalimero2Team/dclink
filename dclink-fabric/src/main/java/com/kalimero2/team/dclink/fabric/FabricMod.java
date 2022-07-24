package com.kalimero2.team.dclink.fabric;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.fabric.command.FabricCommands;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;

public class FabricMod implements DedicatedServerModInitializer {

    private FabricServerAudiences adventure;
    private FabricDCLink fabricDCLink;

    public FabricServerAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return adventure;
    }

    @Override
    public void onInitializeServer() {
        fabricDCLink = new FabricDCLink(this);
        fabricDCLink.init();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> fabricDCLink.setServer(server));
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            MinecraftPlayer minecraftPlayer = fabricDCLink.getMinecraftPlayer(handler.getPlayer().getUUID());
            DCLink.JoinResult joinResult = fabricDCLink.onLogin(minecraftPlayer);
            if(!joinResult.success()){
                handler.disconnect(adventure.toNative(joinResult.message()));
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            try {
                FabricCommands paperCommands = new FabricCommands();
                Commands commands = new Commands(fabricDCLink, paperCommands);
                commands.registerCommands();
                fabricDCLink.getLogger().info("Registered Commands");
            } catch (Exception e) {
                fabricDCLink.getLogger().error("Failed to initialize Commands " + e.getMessage());
            }
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.adventure = FabricServerAudiences.of(server));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> fabricDCLink.load());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> fabricDCLink.shutdown());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> this.adventure = null);
    }
}
