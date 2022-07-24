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
        try{
            Class.forName("com.destroystokyo.paper.PaperConfig");
            getLogger().warning("!!! WARNING !!!");
            getLogger().warning("You are using a Paper based Server Software. Please use the Paper Version of DCLink for better performance.");
            getLogger().warning("Link: ");
            getLogger().warning("!!! WARNING !!!");

        }catch (ClassNotFoundException ignored){

        }


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
