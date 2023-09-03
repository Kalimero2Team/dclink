package com.kalimero2.team.dclink.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.paper.command.PaperCommandHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;


public class PaperDCLink extends DCLink {

    private final PaperPlugin plugin;

    public PaperDCLink(PaperPlugin plugin) {
        this.plugin = plugin;
    }

    public PaperPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        try {
            PaperCommandHandler paperCommandHandler = new PaperCommandHandler(this);
            Commands commands = new Commands(this, paperCommandHandler);
            commands.registerCommands();
            getLogger().info("Registered Commands");
        } catch (Exception e) {
            getLogger().error("Failed to initialize Commands " + e.getMessage());
        }
        super.load();
    }

    @Override
    public UUID getUUID(String username) {
        return plugin.getServer().getOfflinePlayer(username).getUniqueId();
    }

    @Override
    protected String getUserNameViaPlatformMethods(UUID uuid) {
        if (isLoaded()) {
            String name = plugin.getServer().getOfflinePlayer(uuid).getName();
            if (name == null) {
                PlayerProfile profile = plugin.getServer().createProfile(uuid);
                profile.complete(false);
                return profile.getName();
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
    protected void kickPlayer(MinecraftPlayer minecraftPlayer, Component message) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(minecraftPlayer.getUuid());
        if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    offlinePlayer.getPlayer().kick(message);
                }
            }.runTask(plugin);
        }
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
}
