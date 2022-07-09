package com.kalimero2.team.dclink.spigot;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;


public class SpigotDCLink extends DCLink {

    private final SpigotPlugin plugin;

    public SpigotDCLink(SpigotPlugin plugin){
        this.plugin = plugin;
    }

    public SpigotPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public UUID getUUID(String username) {
        return plugin.getServer().getOfflinePlayer(username).getUniqueId();
    }

    @Override
    protected String getUserNameViaPlatformMethods(UUID uuid) {
        if(isLoaded()){
            return plugin.getServer().getOfflinePlayer(uuid).getName();
        }
        return null;
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
        plugin.getServer().shutdown();
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
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(minecraftPlayer.getUuid());
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if (getMessages().minecraftMessages != null) {
                            Component minifiedMessage = getMessages().getMinifiedMessage(getMessages().minecraftMessages.kickUnlinked);
                            offlinePlayer.getPlayer().kickPlayer(LegacyComponentSerializer.legacySection().serialize(minifiedMessage));
                        }
                    }
                }.runTask(plugin);
            }
        }
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
}
