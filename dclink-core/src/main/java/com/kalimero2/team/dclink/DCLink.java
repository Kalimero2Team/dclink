package com.kalimero2.team.dclink;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.storage.Storage;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public abstract class DCLink implements DCLinkApi {

    private final Logger logger = LoggerFactory.getLogger("dclink");
    private DCLinkConfig dcLinkConfig;
    private Storage storage;
    private boolean initialised = false;
    private boolean loaded = false;

    public void init(){
        if(!initialised){
            try{
                dcLinkConfig = new DCLinkConfig(getConfigPath());
            }catch (ConfigurateException e){
                logger.error("Failed to load config", e);
            }
            logger.info("Loaded config");
            if(dcLinkConfig.databaseConfiguration != null){
                storage = new Storage(this, new File(getDataFolder(), dcLinkConfig.databaseConfiguration.getSqliteFile()));
            }else {
                logger.error("No database configuration found");
            }
            logger.info("Loaded storage");
            // TODO: Init Bot
            initialised = true;
        }
    }


    public void load(){
        if(!loaded && initialised){
            // TODO: Enable Bot Listener
            loaded = true;
            MinecraftPlayer minecraftPlayer = getMinecraftPlayer(UUID.fromString("fed832f1-7c49-43af-8a54-5741d14d4e5b"));
            DiscordAccount discordAccount = getDiscordAccount("303949887244992512");
            linkAccounts(minecraftPlayer, discordAccount);
            logger.info(minecraftPlayer.getDiscordAccount().getId());
            logger.info(minecraftPlayer.getDiscordAccount().getLinkedPlayers().stream().toList().get(0).getName());
        }
    }

    public void shutdown(){
        if(loaded && initialised){
            loaded = false;
            // TODO: Shutdown Bot
            storage.close();
            logger.info("Shutdown complete");
        }
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
        return JoinResult.failure(Component.text(DCLinkCodes.addPlayer(minecraftPlayer)));
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
    public abstract String getConfigPath();
    public abstract File getDataFolder();
}
