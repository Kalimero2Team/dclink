package com.kalimero2.team.dclink.impl.discord;

import com.kalimero2.team.dclink.api.discord.DiscordRole;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import java.util.Objects;

public class DiscordRoleImpl implements DiscordRole {

    private final JDA jda;
    private final String discordId;

    public DiscordRoleImpl(JDA jda, String discordId){
        this.jda = jda;
        this.discordId = discordId;
    }
    @Override
    public String getName() {
        Role role = jda.getRoleById(this.discordId);
        if(role != null) {
            return role.getName();
        }
        return null;
    }

    @Override
    public String getId() {
        return this.discordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscordRoleImpl that = (DiscordRoleImpl) o;
        return Objects.equals(discordId, that.discordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discordId);
    }

    @Override
    public String toString() {
        return "DiscordRoleImpl{" +
                "discordId='" + discordId + '\'' +
                '}';
    }
}
