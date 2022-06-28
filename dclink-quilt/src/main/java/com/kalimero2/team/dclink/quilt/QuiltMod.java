package com.kalimero2.team.dclink.quilt;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;

public class QuiltMod implements ModInitializer {

    private QuiltDCLink quiltDCLink;

    @Override
    public void onInitialize(ModContainer mod) {
        ServerLifecycleEvents.STARTING.register(server -> {
            if(quiltDCLink == null) {
                quiltDCLink = new QuiltDCLink(this, server);
                quiltDCLink.init();
            }else{
                throw new IllegalStateException("DCLink Already initialized");
            }
        });

        ServerLifecycleEvents.READY.register(server -> {
            quiltDCLink.load();
        });
        ServerLifecycleEvents.STOPPING.register(server -> {
            quiltDCLink.shutdown();
        });
    }
}
