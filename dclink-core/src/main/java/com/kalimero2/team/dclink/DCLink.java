package com.kalimero2.team.dclink;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.DCLinkApiHolder;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.discord.DiscordRole;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.discord.DiscordBot;
import com.kalimero2.team.dclink.impl.discord.DiscordRoleImpl;
import com.kalimero2.team.dclink.impl.minecraft.MinecraftPlayerImpl;
import com.kalimero2.team.dclink.storage.Storage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public abstract class DCLink implements DCLinkApi {

    private final Logger logger = LoggerFactory.getLogger("dclink");
    private DCLinkMessages dcLinkMessages;
    private DCLinkConfig dcLinkConfig;
    private Storage storage;
    private DiscordBot discordBot;
    private boolean initialised = false;
    private boolean loaded = false;

    public void init() {
        if (!initialised) {
            logger.info("Initialising DCLink");
            DCLinkApiHolder.set(this);

            try {
                dcLinkMessages = new DCLinkMessages(getMessagesFile());
            } catch (ConfigurateException e) {
                logger.error("Failed to load messages", e);
                shutdownServer();
            }
            logger.info("Loaded messages");
            try {
                dcLinkConfig = new DCLinkConfig(getConfigPath());
            } catch (ConfigurateException e) {
                logger.error("Failed to load config", e);
                shutdownServer();
            }
            logger.info("Loaded config");
            if (dcLinkConfig.getDatabaseConfiguration() != null) {
                storage = new Storage(this, new File(getDataFolder(), dcLinkConfig.getDatabaseConfiguration().getSqliteFile()));
            } else {
                logger.error("No database configuration found");
                shutdownServer();
            }
            logger.info("Initialised storage");
            try {
                discordBot = new DiscordBot(this);
            } catch (LoginException | InterruptedException e) {
                logger.error("Failed to load discord bot", e);
                shutdownServer();
                return;
            }
            logger.info("Initialised Discord bot");
            try {
                Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                logger.info("Found Floodgate API");
            } catch (ClassNotFoundException e) {
                logger.info("Floodgate not found, Bedrock players won't be detected");
            }
            initialised = true;
            logger.info("Initialised DCLink");
        }
    }

    public void load() {
        if (!loaded && initialised) {
            loaded = true;
            discordBot.loadFeatures();
            logger.info("Loaded Discord Bot");
        }
    }

    public void shutdown() {
        if (loaded && initialised) {
            loaded = false;
            if (discordBot != null) {
                discordBot.shutdown();
            }
            storage.close();
            logger.info("Shutdown complete");
        }
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    @Override
    public MinecraftPlayer getMinecraftPlayer(UUID uuid) {
        try {
            return storage.getMinecraftPlayer(uuid);
        } catch (SQLException e) {
            logger.error("Error while getting MinecraftPlayer", e);
            return null;
        }
    }

    @Override
    public DiscordAccount getDiscordAccount(String id) {
        try {
            return storage.getDiscordAccount(id);
        } catch (SQLException e) {
            logger.error("Error while getting DiscordAccount", e);
            return null;
        }
    }

    @Override
    public DiscordRole getDiscordRole(String id) {
        return new DiscordRoleImpl(getDiscordBot().getJda(), id);
    }

    @Override
    public boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount) {
        return storage.linkAccounts(minecraftPlayer, discordAccount);
    }

    @Override
    public void unLinkAccounts(DiscordAccount discordAccount) {
        if (dcLinkConfig.getDiscordConfiguration().getLinkRole() != null) {
            DiscordRole linkRole = getDiscordRole(dcLinkConfig.getDiscordConfiguration().getLinkRole());
            discordAccount.removeRole(linkRole);
        }
        if (getConfig().getLinkingConfiguration().isLinkRequired()) {
            discordAccount.getLinkedPlayers().forEach(minecraftPlayer -> kickPlayer(minecraftPlayer, getMessages().getMinifiedMessage(getMessages().getMinecraftMessages().kickUnlinked)));
        }
        storage.unLinkAccounts(discordAccount);
    }

    @Override
    public void unLinkAccount(MinecraftPlayer minecraftPlayer) {
        if (dcLinkConfig.getDiscordConfiguration().getLinkRole() != null) {
            DiscordRole linkRole = getDiscordRole(dcLinkConfig.getDiscordConfiguration().getLinkRole());
            minecraftPlayer.getDiscordAccount().removeRole(linkRole);
        }
        if (getConfig().getLinkingConfiguration().isLinkRequired()) {
            kickPlayer(minecraftPlayer, getMessages().getMinifiedMessage(getMessages().getMinecraftMessages().kickUnlinked));
        }
        storage.unLinkAccount(minecraftPlayer);
    }

    public boolean isBedrock(MinecraftPlayer minecraftPlayer) {
        return isBedrock(minecraftPlayer.getUuid());
    }

    public boolean isBedrock(UUID uuid) {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        } catch (ClassNotFoundException e) {
            logger.info("Floodgate not found, Bedrock players won't be detected");
            return false;
        }
    }

    public JoinResult onLogin(MinecraftPlayer minecraftPlayer) {
        if (!minecraftPlayer.isLinked() && dcLinkConfig.getLinkingConfiguration().isLinkRequired()) {
            Component code = dcLinkMessages.getMinifiedMessage(dcLinkMessages.getMinecraftMessages().linkCodeMessage, Placeholder.unparsed("code", DCLinkCodes.addPlayer(minecraftPlayer)));
            return JoinResult.failure(code);
        } else {
            return JoinResult.success(null);
        }
    }

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Logger getLogger() {
        return logger;
    }

    public DCLinkConfig getConfig() {
        return dcLinkConfig;
    }

    public DCLinkMessages getMessages() {
        return dcLinkMessages;
    }

    public UUID getUUID(String username) {
        try {
            return storage.getUUIDByLastKnownName(username);
        } catch (SQLException e) {
            getLogger().error("Minecraft account with username: \"" + username + "\" does not exist");
            return null;
        }
    }

    protected abstract void kickPlayer(MinecraftPlayer minecraftPlayer, Component message);

    protected abstract String getConfigPath();

    protected abstract String getMessagesFile();

    protected abstract void shutdownServer();

    public abstract File getDataFolder();

    protected Storage getStorage() {
        return storage;
    }

    public record JoinResult(Component message, boolean success) {
        public static JoinResult success(Component message) {
            return new JoinResult(message, true);
        }

        public static JoinResult failure(Component message) {
            return new JoinResult(message, false);
        }
    }
}
