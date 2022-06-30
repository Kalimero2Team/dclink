package com.kalimero2.team.dclink.impl.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.discord.DiscordRole;

import java.util.Collection;
import java.util.Objects;

public abstract class DiscordAccountImpl implements DiscordAccount {

    private final DCLink dcLink;
    private final String discordId;

    public DiscordAccountImpl(DCLink dcLink, String discordId) {
        this.dcLink = dcLink;
        this.discordId = discordId;
    }

    @Override
    public String getName() {
        //TODO: Discord API get Name (and cache it)
        return null;
    }

    @Override
    public String getDiscriminator() {
        //TODO: Discord API get Discriminator (and cache it)
        return null;
    }

    @Override
    public String getId() {
        return this.discordId;
    }

    @Override
    public Collection<DiscordRole> getRoles() {
        //TODO: Discord API get Roles
        return null;
    }

    @Override
    public boolean isMemberOfGuild() {
        //TODO: Discord API get Guild and check if Account is Member
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscordAccountImpl that = (DiscordAccountImpl) o;
        return Objects.equals(discordId, that.discordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discordId);
    }

    @Override
    public String toString() {
        return "DiscordAccountImpl{" +
                "discordId='" + discordId + '\'' +
                '}';
    }
}
