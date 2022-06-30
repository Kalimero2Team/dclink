package com.kalimero2.team.dclink.quilt;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;

public class QuiltMod implements ModInitializer {

    private FabricServerAudiences adventure;
    private QuiltDCLink quiltDCLink;

    public FabricServerAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return adventure;
    }

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
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            MinecraftPlayer minecraftPlayer = quiltDCLink.getMinecraftPlayer(handler.getPlayer().getUuid());
            DCLink.JoinResult joinResult = quiltDCLink.onLogin(minecraftPlayer);
            if(!joinResult.success()){
                handler.disconnect(adventure.toNative(joinResult.message()));
            }
        });
        ServerLifecycleEvents.STARTING.register(server -> {
            this.adventure = FabricServerAudiences.of(server);
        });
        ServerLifecycleEvents.READY.register(server -> {
            quiltDCLink.load();
        });
        ServerLifecycleEvents.STOPPING.register(server -> {
            quiltDCLink.shutdown();
        });
        ServerLifecycleEvents.STOPPED.register(server -> {
            this.adventure = null;
        });
    }
}
