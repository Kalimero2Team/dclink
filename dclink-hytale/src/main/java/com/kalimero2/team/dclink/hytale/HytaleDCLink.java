package com.kalimero2.team.dclink.hytale;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.Universe;
import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.api.game.GameType;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.hytale.command.HytaleCommandHandler;
import com.kalimero2.team.dclink.hytale.logger.HytaleLoggerWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.slf4j.Logger;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class HytaleDCLink extends DCLink {

    private final HytalePlugin plugin;
    private final Logger logger;

    public HytaleDCLink(HytalePlugin plugin) {
        this.plugin = plugin;
        this.logger = new HytaleLoggerWrapper(plugin.getLogger());
    }

    public HytalePlugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        if (isInitialised()) {
            try {
                HytaleCommandHandler hytaleCommandHandler = new HytaleCommandHandler();
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
    public GameType getGameType() {
        return GameType.HYTALE;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    @SuppressWarnings("removal")
    protected void kickPlayer(GamePlayer gamePlayer, Component message) {
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            var playerRef = Universe.get().getPlayer(gamePlayer.getUuid());
            if (playerRef != null) {
                PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
                playerRef.getPacketHandler().disconnect(serializer.serialize(message));
            }
        }, 1, TimeUnit.SECONDS);
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
