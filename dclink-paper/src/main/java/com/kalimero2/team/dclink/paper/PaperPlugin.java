package com.kalimero2.team.dclink.paper;

import com.kalimero2.team.dclink.paper.commands.CommandManager;
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
        getServer().getPluginManager().registerEvents(new PaperDCLinkListener(paperDCLink), this);
        try {
            new CommandManager(paperDCLink);
            paperDCLink.getLogger().info("Registered Commands");
        } catch (Exception e) {
            paperDCLink.getLogger().error("Failed to initialize Commands" + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        paperDCLink.shutdown();
    }
}
