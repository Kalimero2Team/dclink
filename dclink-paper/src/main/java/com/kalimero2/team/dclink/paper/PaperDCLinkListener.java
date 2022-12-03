package com.kalimero2.team.dclink.paper;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PaperDCLinkListener implements Listener {

    private final PaperDCLink paperDCLink;

    public PaperDCLinkListener(PaperDCLink paperDCLink) {
        this.paperDCLink = paperDCLink;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        MinecraftPlayer minecraftPlayer = paperDCLink.getMinecraftPlayer(event.getPlayerProfile().getId());
        DCLink.JoinResult joinResult = paperDCLink.onLogin(minecraftPlayer);
        if (joinResult.success()) {
            event.allow();
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, joinResult.message());
        }
    }

}
