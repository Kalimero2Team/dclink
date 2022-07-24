package com.kalimero2.team.dclink.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "dclink-velocity", name = "DCLink", version = "1.0.2-SNAPSHOT", authors = {"byquanton"})
public class VelocityPlugin {

    private final VelocityDCLink dclink;
    private final ProxyServer server;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.dclink = new VelocityDCLink(this, server, dataDirectory);
        this.server = server;
        dclink.init();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        dclink.load();
        server.getEventManager().register(this, new VelocityDCLinkListener(dclink));
    }

}
