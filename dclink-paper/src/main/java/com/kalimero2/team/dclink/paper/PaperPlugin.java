package com.kalimero2.team.dclink.paper;

import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlugin extends JavaPlugin {

    private PaperDCLink paperDCLink;

    @Override
    public void onLoad() {
        if(paperDCLink == null){
            paperDCLink = new PaperDCLink();
            paperDCLink.load();
        }else {
            throw new IllegalStateException("DCLink Already initialized");
        }
    }

    @Override
    public void onEnable() {
        paperDCLink.enable();
    }

    @Override
    public void onDisable() {
        paperDCLink.disable();
    }
}
