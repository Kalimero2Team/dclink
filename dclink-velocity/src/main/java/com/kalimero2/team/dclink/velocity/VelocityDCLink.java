package com.kalimero2.team.dclink.velocity;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.velocity.command.VelocityCommandHandler;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

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
    public UUID getUUID(String username) {
        Optional<Player> player = server.getPlayer(username);
        if (player.isPresent()) {
            return player.get().getUniqueId();
        }
        // On Velocity we don't have a way to get the UUID of an offline player, so we get it from the database
        try {
            return getStorage().getUUIDByLastKnownName(username);
        } catch (SQLException e) {
            return null;
        }

    }

    @Override
    protected void kickPlayer(MinecraftPlayer minecraftPlayer, Component message) {
        server.getPlayer(minecraftPlayer.getUuid()).ifPresent(player -> player.disconnect(message));
    }

    @Override
    protected String getUserNameViaPlatformMethods(UUID uuid) {
        Optional<Player> player = server.getPlayer(uuid);
        return player.map(Player::getUsername).orElse(null);
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
