package com.kalimero2.team.dclink.neoforge;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.neoforge.command.NeoForgeCommandHandler;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(NeoForgeMod.MODID)
public class NeoForgeMod {
    public static final String MODID = "dclink_neoforge";

    private final NeoForgeDCLink neoForgeDCLink;
    private MinecraftServerAudiences adventure;


    public MinecraftServerAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return adventure;
    }


    public NeoForgeMod(IEventBus modEventBus, ModContainer modContainer) {
        neoForgeDCLink = new NeoForgeDCLink(this);
        neoForgeDCLink.init();

        NeoForgeCommandHandler neoForgeCommandHandler = new NeoForgeCommandHandler(neoForgeDCLink);
        Commands commands = new Commands(neoForgeDCLink, neoForgeCommandHandler);
        commands.registerCommands();

        neoForgeDCLink.getLogger().info("Registered Commands");

        modEventBus.addListener((ServerStartingEvent e) -> this.adventure = MinecraftServerAudiences.of(e.getServer()));
        modEventBus.addListener((ServerStartedEvent e) -> neoForgeDCLink.load());
        modEventBus.addListener((ServerStoppingEvent e) -> neoForgeDCLink.shutdown());
        modEventBus.addListener((ServerStoppedEvent e) -> this.adventure = null);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DCLink.JoinResult joinResult = neoForgeDCLink.onLogin(player.getUUID(), player.getGameProfile().getName());
            if (!joinResult.success()) {
                player.connection.disconnect(adventure.asNative(joinResult.message()));
            }
        }

    }
}
