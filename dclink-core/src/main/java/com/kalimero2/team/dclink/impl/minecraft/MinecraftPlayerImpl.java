package com.kalimero2.team.dclink.impl.minecraft;

import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.Objects;
import java.util.UUID;

public abstract class MinecraftPlayerImpl implements MinecraftPlayer {

    private final UUID uuid;
    private String name;

    public MinecraftPlayerImpl(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
                ", name=" + name +
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
