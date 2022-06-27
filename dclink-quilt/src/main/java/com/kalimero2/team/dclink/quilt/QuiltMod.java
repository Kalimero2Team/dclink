package com.kalimero2.team.dclink.quilt;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class QuiltMod implements ModInitializer {

    private QuiltDCLink quiltDCLink;

    @Override
    public void onInitialize(ModContainer mod) {
        if(quiltDCLink == null) {
            quiltDCLink = new QuiltDCLink();
            quiltDCLink.load();
        }else{
            throw new IllegalStateException("DCLink Already initialized");
        }

    }
}
