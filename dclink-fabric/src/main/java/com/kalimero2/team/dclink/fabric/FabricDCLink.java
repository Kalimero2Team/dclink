package com.kalimero2.team.dclink.fabric;

import com.kalimero2.team.dclink.MinecraftDCLink;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;

public class FabricDCLink extends MinecraftDCLink {

    private final FabricMod mod;
    private MinecraftServer server;

    public FabricDCLink(FabricMod mod) {
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
    protected void kickPlayer(GamePlayer gamePlayer, Component message) {
        ServerPlayer player = server.getPlayerList().getPlayer(gamePlayer.getUuid());
        if (player != null) {
            player.connection.disconnect(mod.adventure().asNative(message));
        }
    }

    @Override
    protected String getConfigPath() {
        return new File(getDataFolder(), "dclink.conf").getAbsolutePath();
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

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
