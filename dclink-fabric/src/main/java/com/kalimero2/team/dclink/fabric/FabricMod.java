package com.kalimero2.team.dclink.fabric;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.fabric.command.FabricCommandHandler;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.level.ServerPlayer;

public class FabricMod implements DedicatedServerModInitializer {

    private MinecraftServerAudiences adventure;
    private FabricDCLink fabricDCLink;

    public MinecraftServerAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return adventure;
    }

    @Override
    public void onInitializeServer() {
        fabricDCLink = new FabricDCLink(this);
        fabricDCLink.init();

        FabricCommandHandler fabricCommandHandler = new FabricCommandHandler(fabricDCLink);
        Commands commands = new Commands(fabricDCLink, fabricCommandHandler);
        commands.registerCommands();
        fabricDCLink.getLogger().info("Registered Commands");

        ServerLifecycleEvents.SERVER_STARTING.register(server -> fabricDCLink.setServer(server));
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            DCLink.JoinResult joinResult = fabricDCLink.onLogin(player.getUUID(), player.getGameProfile().getName());
            if (!joinResult.success()) {
                handler.disconnect(adventure.asNative(joinResult.message()));
            }
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.adventure = MinecraftServerAudiences.of(server));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> fabricDCLink.load());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> fabricDCLink.shutdown());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> this.adventure = null);
    }
}
