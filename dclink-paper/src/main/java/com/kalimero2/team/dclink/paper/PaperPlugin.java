package com.kalimero2.team.dclink.paper;

import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlugin extends JavaPlugin {

    private PaperDCLink paperDCLink;

    @Override
    public void onLoad() {
        if(paperDCLink == null){
            paperDCLink = new PaperDCLink(this);
            paperDCLink.init();
        }else {
            throw new IllegalStateException("DCLink Already initialized");
        }
    }

    @Override
    public void onEnable() {
        paperDCLink.load();
    }

    @Override
    public void onDisable() {
        paperDCLink.shutdown();
    }
}
