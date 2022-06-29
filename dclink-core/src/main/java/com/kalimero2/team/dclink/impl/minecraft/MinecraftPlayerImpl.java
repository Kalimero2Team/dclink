package com.kalimero2.team.dclink.impl.minecraft;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.UUID;

public abstract class MinecraftPlayerImpl implements MinecraftPlayer {

    private final DCLink dcLink;
    private final UUID uuid;

    public MinecraftPlayerImpl(DCLink dcLink, UUID uuid) {
        this.dcLink = dcLink;
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return dcLink.getUsername(this.uuid);
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public boolean isLinked() {
        return (getDiscordAccount() != null);
    }

}
