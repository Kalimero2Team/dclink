package com.kalimero2.team.dclink.hytale;

import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.server.core.modules.accesscontrol.AccessControlModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class HytalePlugin extends JavaPlugin {

    public static final PluginManifest MANIFEST = PluginManifest
            .corePlugin(HytalePlugin.class)
            .depends(AccessControlModule.class)
            .build();

    private HytaleDCLink dcLink;

    public HytalePlugin(JavaPluginInit init) {
        super(init);
    }


    @Override
    protected void setup() {
        dcLink = new HytaleDCLink(this);
        dcLink.init();
        
        HytaleListener listener = new HytaleListener(dcLink);
        listener.register();
    }

    @Override
    protected void start() {
        dcLink.load();
    }

    @Override
    protected void shutdown() {
        if (dcLink != null) {
            dcLink.shutdown();
        }
    }
}
