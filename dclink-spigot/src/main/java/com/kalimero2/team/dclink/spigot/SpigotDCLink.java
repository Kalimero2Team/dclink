package com.kalimero2.team.dclink.spigot;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.spigot.command.SpigotCommands;
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
    public void load() {
        try {
            SpigotCommands paperCommands = new SpigotCommands(this);
            Commands commands = new Commands(this, paperCommands);
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
    protected void kickPlayer(MinecraftPlayer minecraftPlayer, Component message) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(minecraftPlayer.getUuid());
        if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
            new BukkitRunnable(){
                @Override
                public void run() {
                    offlinePlayer.getPlayer().kickPlayer(LegacyComponentSerializer.legacySection().serialize(message));
                }
            }.runTask(plugin);
        }
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
}
