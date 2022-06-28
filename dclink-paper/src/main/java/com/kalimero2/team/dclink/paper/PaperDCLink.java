package com.kalimero2.team.dclink.paper;

import com.kalimero2.team.dclink.DCLink;

import java.io.File;
import java.util.UUID;


public class PaperDCLink extends DCLink {

    private final PaperPlugin plugin;

    public PaperDCLink(PaperPlugin plugin){
        this.plugin = plugin;
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
    public String getUsername(UUID uuid) {
        if(isLoaded()){
            return plugin.getServer().getOfflinePlayer(uuid).getName();
        }
        return null;
    }

    @Override
    public String getConfigPath() {
        return new File(plugin.getDataFolder(), "dclink.conf").getAbsolutePath();
    }
}
