package com.kalimero2.team.dclink;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class DCLink implements DCLinkApi {

    private final Logger logger = LoggerFactory.getLogger("dclink");
    private boolean initialised = false;
    private boolean loaded = false;

    public void init(){
        if(!initialised){
            // TODO: Load Config
            // TODO: Connect to Database
            // TODO: Init Bot
            initialised = true;
        }
    }

    public void load(){
        if(!loaded && initialised){
            // TODO: Enable Bot Listener
            loaded = true;
        }
    }

    public void shutdown(){
        if(loaded && initialised){
            // TODO: Shutdown Bot
            // TODO: Close Database connections
            loaded = false;
        }
    }

    @Override
    public MinecraftPlayer getMinecraftPlayer(UUID uuid) {
        return null;
    }

    @Override
    public DiscordAccount getDiscordMember(String id) {
        return null;
    }

    @Override
    public boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount) {
        return false;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public abstract String getUsername(UUID uuid);

}
