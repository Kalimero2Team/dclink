package com.kalimero2.team.dclink.hytale;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.kalimero2.team.dclink.DCLink;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class HytaleListener {
    private final HytaleDCLink dcLink;

    public HytaleListener(HytaleDCLink dcLink) {
        this.dcLink = dcLink;
    }

    public void register() {
        dcLink.getPlugin().getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);
    }

    @SuppressWarnings("removal")
    public void onPlayerConnect(PlayerConnectEvent event) {
        DCLink.JoinResult joinResult = dcLink.onLogin(event.getPlayer().getUuid(), event.getPlayer().getDisplayName());
        if (!joinResult.success()) {
            String plain = PlainTextComponentSerializer.plainText().serialize(joinResult.message());
            event.getPlayer().getPlayerRef().getPacketHandler().disconnect(plain);
        }
    }
}
