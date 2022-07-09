package com.kalimero2.team.dclink.velocity;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.nio.file.Path;
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
    public String getUsername(UUID uuid) {
        Optional<Player> player = server.getPlayer(uuid);
        return player.map(Player::getUsername).orElse(null);
    }

    @Override
    public UUID getUUID(String username) {
        Optional<Player> player = server.getPlayer(username);
        return player.map(Player::getUniqueId).orElse(null);
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
    public void unLinkAccount(MinecraftPlayer minecraftPlayer) {
        kickUnlinked(minecraftPlayer);
        super.unLinkAccount(minecraftPlayer);
    }

    @Override
    public void unLinkAccounts(DiscordAccount discordAccount) {
        discordAccount.getLinkedPlayers().forEach(this::kickUnlinked);
        super.unLinkAccounts(discordAccount);
    }

    private void kickUnlinked(MinecraftPlayer minecraftPlayer) {
        if (getConfig().linkingConfiguration != null && getConfig().linkingConfiguration.linkRequired) {
            Optional<Player> player = server.getPlayer(minecraftPlayer.getUuid());
            if(player.isPresent()){
                assert getMessages().minecraftMessages != null;
                player.get().disconnect(getMessages().getMinifiedMessage(getMessages().minecraftMessages.kickUnlinked));
            }
        }
    }

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }
}
