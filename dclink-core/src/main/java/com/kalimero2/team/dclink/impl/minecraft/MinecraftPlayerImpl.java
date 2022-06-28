package com.kalimero2.team.dclink.impl.minecraft;

import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.UUID;

public class MinecraftPlayerImpl implements MinecraftPlayer {
    @Override
    public String getName() {
        // TODO: Use the Platform's Native Resolver and fall back to Mojang API
        return "";
    }

    @Override
    public UUID getUuid() {
        // TODO: Get UUID String from Database and Convert to UUID Object
        return UUID.randomUUID();
    }

    @Override
    public boolean isLinked() {
        return false;
    }
}
