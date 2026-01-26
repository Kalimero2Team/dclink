package com.kalimero2.team.dclink.hytale;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent;
import com.kalimero2.team.dclink.DCLink;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class HytaleListener {
    private final HytaleDCLink dcLink;

    public HytaleListener(HytaleDCLink dcLink) {
        this.dcLink = dcLink;
    }

    public void register() {
        dcLink.getPlugin().getEventRegistry().register(EventPriority.FIRST, PlayerSetupConnectEvent.class, this::onPlayerConnect);
    }

    public void onPlayerConnect(PlayerSetupConnectEvent event) {
        if (event.isCancelled()) return;

        DCLink.JoinResult joinResult = dcLink.onLogin(event.getUuid(), event.getUsername());
        if (!joinResult.success()) {
            String plain = PlainTextComponentSerializer.plainText().serialize(joinResult.message());
            event.setReason(plain);
            event.setCancelled(true);
        }
    }
}
