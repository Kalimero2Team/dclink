package com.kalimero2.team.dclink.hytale;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.Universe;
import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.GamePlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.hytale.command.HytaleCommandHandler;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class HytaleDCLink extends DCLink {

    private final HytalePlugin plugin;
    private final Logger logger;

    public HytaleDCLink(HytalePlugin plugin) {
        this.plugin = plugin;
        this.logger = LoggerFactory.getLogger("DCLink");
    }

    public HytalePlugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        if (isInitialised()) {
            try {
                HytaleCommandHandler hytaleCommandHandler = new HytaleCommandHandler(this);
                Commands commands = new Commands(this, hytaleCommandHandler);
                commands.registerCommands();
                getLogger().info("Registered Commands");
            } catch (Exception e) {
                getLogger().error("Failed to initialize Commands {}", e.getMessage());
            }
        }
        super.load();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    @SuppressWarnings("removal")
    protected void kickPlayer(GamePlayer gamePlayer, Component message) {
        var playerRef = Universe.get().getPlayer(gamePlayer.getUuid());
        if (playerRef != null) {
            playerRef.getPacketHandler().disconnect("Kicked by DCLink");
        }
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
        HytaleServer.get().shutdownServer();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataDirectory().toFile();
    }
}
