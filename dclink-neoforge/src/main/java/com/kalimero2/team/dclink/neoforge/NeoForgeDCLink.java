package com.kalimero2.team.dclink.neoforge;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;

public class NeoForgeDCLink extends DCLink {

    private final NeoForgeMod mod;
    private MinecraftServer server;

    public NeoForgeDCLink(NeoForgeMod mod) {
        this.mod = mod;
    }

    public NeoForgeMod getMod() {
        return mod;
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    protected void kickPlayer(MinecraftPlayer minecraftPlayer, Component message) {
        ServerPlayer player = server.getPlayerList().getPlayer(minecraftPlayer.getUuid());
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
        return FMLPaths.CONFIGDIR.get().toFile();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
