package com.kalimero2.team.dclink.impl.discord;

import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.discord.DiscordRole;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.Collection;

public class DiscordAccountImpl implements DiscordAccount {
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
        //TODO: ID used to find Account in Database (and use on Discord)
        return null;
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
    public Collection<MinecraftPlayer> getLinkedPlayers() {
        //TODO: Get Linked Account from Database
        return null;
    }
}
