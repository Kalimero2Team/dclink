package com.kalimero2.team.dclink.paper;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.paper.command.PaperCommandHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;


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
            try {
                // On Paper we can only kick player from the main thread
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        offlinePlayer.getPlayer().kick(message);
                    }
                }.runTask(plugin);
            } catch (UnsupportedOperationException ignored) { // Folia
                offlinePlayer.getPlayer().kick(message);
            }
        }
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    public boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
