package com.kalimero2.team.dclink.velocity;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.GamePlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.velocity.command.VelocityCommandHandler;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.nio.file.Path;

public class VelocityDCLink extends DCLink {

    private final VelocityPlugin plugin;
    private final ProxyServer server;
    private final Path dataDirectory;

    public VelocityDCLink(VelocityPlugin plugin, ProxyServer server, Path dataDirectory) {
        this.plugin = plugin;
        this.server = server;
        this.dataDirectory = dataDirectory;
    }

    public VelocityPlugin getPlugin() {
        return plugin;
    }

    public ProxyServer getServer() {
        return server;
    }

    @Override
    public void load() {
        if (isInitialised()) {
            try {
                VelocityCommandHandler velocityCommandHandler = new VelocityCommandHandler(this);
                Commands commands = new Commands(this, velocityCommandHandler);
                commands.registerCommands();
                getLogger().info("Registered Commands");
            } catch (Exception e) {
                getLogger().error("Failed to initialize Commands " + e.getMessage());
            }
        }
        super.load();
    }

    @Override
    protected void kickPlayer(GamePlayer gamePlayer, Component message) {
        server.getPlayer(gamePlayer.getUuid()).ifPresent(player -> player.disconnect(message));
    }

    @Override
    protected String getConfigPath() {
        return new File(getDataFolder(), "dclink.conf").getAbsolutePath();
    }

    @Override
    protected String getMessagesFile() {
        return new File(getDataFolder(), "messages.conf").getAbsolutePath();
    }

    @Override
    protected void shutdownServer() {
        server.shutdown();
    }

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }
}
