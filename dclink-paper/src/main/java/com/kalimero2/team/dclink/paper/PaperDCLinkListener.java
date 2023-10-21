package com.kalimero2.team.dclink.paper;

import com.kalimero2.team.dclink.DCLink;
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
        // TODO: Check if getName isn't null (probably the case)
        DCLink.JoinResult joinResult = paperDCLink.onLogin(event.getUniqueId(), event.getPlayerProfile().getName());
        if (joinResult.success()) {
            event.allow();
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, joinResult.message());
        }
    }

}
