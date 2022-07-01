package com.kalimero2.team.dclink;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.discord.DiscordBot;
import com.kalimero2.team.dclink.storage.Storage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public abstract class DCLink implements DCLinkApi {

    private final Logger logger = LoggerFactory.getLogger("dclink");
    private DCLinkMessages dcLinkMessages;
    private DCLinkConfig dcLinkConfig;
    private Storage storage;
    private DiscordBot discordBot;
    private boolean initialised = false;
    private boolean loaded = false;

    public void init(){
        if(!initialised){
            try{
                dcLinkMessages = new DCLinkMessages(getMessagesFile());
            }catch (ConfigurateException e){
                logger.error("Failed to load messages", e);
                shutdownServer();
            }
            logger.info("Loaded messages");
            try{
                dcLinkConfig = new DCLinkConfig(getConfigPath());
            }catch (ConfigurateException e){
                logger.error("Failed to load config", e);
                shutdownServer();
            }
            logger.info("Loaded config");
            if(dcLinkConfig.databaseConfiguration != null){
                storage = new Storage(this, new File(getDataFolder(), dcLinkConfig.databaseConfiguration.sqliteFile));
            }else {
                logger.error("No database configuration found");
                shutdownServer();
            }
            logger.info("Initialised storage");
            try {
                discordBot = new DiscordBot(this);
            } catch (LoginException | InterruptedException e) {
                logger.error("Failed to load discord bot", e);
                shutdownServer();
            }
            logger.info("Initialised Discord bot");
            initialised = true;
        }
    }


    public void load(){
        if(!loaded && initialised){
            loaded = true;
            discordBot.loadFeatures();
            logger.info("Loaded Discord Bot");
        }
    }

    public void shutdown(){
        if(loaded && initialised){
            loaded = false;
            if(discordBot != null){
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
    public boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount) {
        return storage.linkAccounts(minecraftPlayer, discordAccount);
    }
    @Override
    public void unLinkAccounts(DiscordAccount discordAccount) {
        storage.unLinkAccounts(discordAccount);
    }
    @Override
    public void unLinkAccount(MinecraftPlayer minecraftPlayer) {
        storage.unLinkAccount(minecraftPlayer);
    }

    public Logger getLogger() {
        return logger;
    }
    public DCLinkConfig getConfig() {
        return dcLinkConfig;
    }
    public boolean isInitialised() {
        return initialised;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isBedrock(MinecraftPlayer minecraftPlayer){
        return isBedrock(minecraftPlayer.getUuid());
    }
    public boolean isBedrock(UUID uuid){
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgateId(uuid);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public JoinResult onLogin(MinecraftPlayer minecraftPlayer){
        if(!minecraftPlayer.isLinked() && Objects.requireNonNull(dcLinkConfig.linkingConfiguration).linkRequired){
            Component code = dcLinkMessages.getMinifiedMessage(dcLinkMessages.minecraftMessages.linkCodeMessage, Placeholder.unparsed("code",DCLinkCodes.addPlayer(minecraftPlayer)));
            return JoinResult.failure(code);
        }else {
            return JoinResult.success(null);
        }
    }

    public DCLinkMessages getMessages() {
        return dcLinkMessages;
    }

    public record JoinResult(Component message, boolean success) {
        public static JoinResult success(Component message) {
            return new JoinResult(message, true);
        }
        public static JoinResult failure(Component message) {
            return new JoinResult(message, false);
        }
    }

    public abstract String getUsername(UUID uuid);
    public abstract UUID getUUID(String username);
    protected abstract String getConfigPath();
    protected abstract String getMessagesFile();
    protected abstract void shutdownServer();
    public abstract File getDataFolder();
}
