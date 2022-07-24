package com.kalimero2.team.dclink.spigot;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlugin extends JavaPlugin {

    private BukkitAudiences adventure;

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private SpigotDCLink spigotDCLink;

    @Override
    public void onLoad() {
        if(spigotDCLink == null){
            spigotDCLink = new SpigotDCLink(this);
            spigotDCLink.init();
        }else {
            throw new IllegalStateException("DCLink Already initialized");
        }
    }

    @Override
    public void onEnable() {
        spigotDCLink.load();
        this.adventure = BukkitAudiences.create(this);
        getServer().getPluginManager().registerEvents(new SpigotDCLinkListener(spigotDCLink), this);
        try {
            spigotDCLink.getLogger().info("Registered Commands");
        } catch (Exception e) {
            spigotDCLink.getLogger().error("Failed to initialize Commands" + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        spigotDCLink.shutdown();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
