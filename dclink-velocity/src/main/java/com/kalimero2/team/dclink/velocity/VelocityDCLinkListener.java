package com.kalimero2.team.dclink.velocity;

import com.kalimero2.team.dclink.DCLink;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.Player;

public class VelocityDCLinkListener {

    private final VelocityDCLink velocityDCLink;

    public VelocityDCLinkListener(VelocityDCLink velocityDCLink) {
        this.velocityDCLink = velocityDCLink;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onJoin(LoginEvent event) {
        Player player = event.getPlayer();
        DCLink.JoinResult joinResult = velocityDCLink.onLogin(player.getUniqueId(), player.getUsername());
        if (joinResult.success()) {
            event.setResult(ResultedEvent.ComponentResult.allowed());
        } else {
            event.setResult(ResultedEvent.ComponentResult.denied(joinResult.message()));
        }
    }


    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        velocityDCLink.shutdown();
    }

}
