package com.kalimero2.team.dclink.quilt;

import com.kalimero2.team.dclink.DCLink;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.UUID;

public class QuiltDCLink extends DCLink {

    private final QuiltMod mod;
    private final MinecraftServer server;

    public QuiltDCLink(QuiltMod mod, MinecraftServer server){
        this.mod = mod;
        this.server = server;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public String getUsername(UUID uuid) {
        if(isLoaded()){
            Optional<GameProfile> optionalGameProfile = server.getUserCache().getByUuid(uuid);
            if(optionalGameProfile.isPresent()){
                return optionalGameProfile.get().getName();
            }else{
                // TODO: Floodgate get Username
                // TODO: Authlib get Username
            }
        }
        return null;
    }
}
