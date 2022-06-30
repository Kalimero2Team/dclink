package com.kalimero2.team.dclink.impl.minecraft;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.Objects;
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

    @Override
    public String toString() {
        return "MinecraftPlayerImpl{" +
                "uuid=" + uuid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinecraftPlayerImpl that = (MinecraftPlayerImpl) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
