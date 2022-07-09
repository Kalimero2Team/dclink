package com.kalimero2.team.dclink.spigot;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class SpigotDCLinkListener implements Listener {

    private final SpigotDCLink spigotDCLink;

    public SpigotDCLinkListener(SpigotDCLink spigotDCLink) {
        this.spigotDCLink = spigotDCLink;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event){
        MinecraftPlayer minecraftPlayer = spigotDCLink.getMinecraftPlayer(event.getUniqueId());
        DCLink.JoinResult joinResult = spigotDCLink.onLogin(minecraftPlayer);
        if(joinResult.success()) {
            event.allow();
        }else{
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, LegacyComponentSerializer.legacySection().serialize(joinResult.message()));
        }
    }

}
