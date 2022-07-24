package com.kalimero2.team.dclink.fabric;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.fabric.command.FabricCommands;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class FabricDCLink extends DCLink {

    private final FabricMod mod;
    private MinecraftServer server;

    public FabricDCLink(FabricMod mod){
        this.mod = mod;
    }

    public FabricMod getMod() {
        return mod;
    }


    @Override
    public void load() {
        super.load();
    }


    @Override
    public UUID getUUID(String username) {
        if(isLoaded()){
            Optional<GameProfile> gameProfile = server.getProfileCache().get(username);
            if(gameProfile.isPresent()){
                return gameProfile.get().getId();
            }
        }
        return null;
    }

    @Override
    protected void kickPlayer(MinecraftPlayer minecraftPlayer, Component message) {
        ServerPlayer player = server.getPlayerList().getPlayer(minecraftPlayer.getUuid());
        if(player != null){
            player.connection.disconnect(mod.adventure().toNative(message));
        }
    }

    @Override
    protected String getUserNameViaPlatformMethods(UUID uuid) {
        if(isLoaded()){
            Optional<GameProfile> optionalGameProfile = server.getProfileCache().get(uuid);
            if(optionalGameProfile.isPresent()){
                return optionalGameProfile.get().getName();
            }else {
                MinecraftSessionService sessionService = server.getSessionService();
                GameProfile gameProfile = sessionService.fillProfileProperties(new GameProfile(uuid, null), true);
                return gameProfile.getName();
            }
        }
        return null;
    }

    @Override
    protected String getConfigPath() {
        return new File(getDataFolder(),"dclink.conf").getAbsolutePath();
    }

    @Override
    protected String getMessagesFile() {
        return new File(getDataFolder(), "dclink_messages.conf").getAbsolutePath();
    }

    @Override
    protected void shutdownServer() {
        server.close();
    }

    @Override
    public File getDataFolder() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
