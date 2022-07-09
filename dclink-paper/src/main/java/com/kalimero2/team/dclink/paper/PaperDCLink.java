package com.kalimero2.team.dclink.paper;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.server.MinecraftServer;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;


public class PaperDCLink extends DCLink {

    private final PaperPlugin plugin;

    public PaperDCLink(PaperPlugin plugin){
        this.plugin = plugin;
    }

    public PaperPlugin getPlugin() {
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
            String name = plugin.getServer().getOfflinePlayer(uuid).getName();
            if(name == null){
                MinecraftSessionService sessionService = MinecraftServer.getServer().getSessionService();
                try{
                    GameProfile gameProfile = sessionService.fillProfileProperties(new GameProfile(uuid, null), true);
                    return gameProfile.getName();
                }catch (IllegalArgumentException ignored){
                }
            }
            return name;
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
                            offlinePlayer.getPlayer().kick(getMessages().getMinifiedMessage(getMessages().minecraftMessages.kickUnlinked));
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
