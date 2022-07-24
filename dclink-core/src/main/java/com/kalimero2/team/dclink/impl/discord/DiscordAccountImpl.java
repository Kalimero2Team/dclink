package com.kalimero2.team.dclink.impl.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.discord.DiscordRole;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class DiscordAccountImpl implements DiscordAccount {
    private final JDA jda;
    private final DCLink dcLink;
    private final String discordId;

    public DiscordAccountImpl(DCLink dcLink, String discordId) {
        this.jda = dcLink.getDiscordBot().getJda();
        this.dcLink = dcLink;
        this.discordId = discordId;
    }

    @Override
    public String getName() {
        if(this.discordId == null) {
            return null;
        }
        User user = jda.retrieveUserById(this.discordId).complete();
        return user.getName();
    }

    @Override
    public String getDiscriminator() {
        if(this.discordId == null) {
            return null;
        }
        User user = jda.retrieveUserById(this.discordId).complete();
        return user.getDiscriminator();
    }

    @Override
    public String getId() {
        return this.discordId;
    }

    @Override
    public Collection<DiscordRole> getRoles() {
        if(this.discordId == null) {
            return null;
        }

        List<DiscordRole> roleList = new ArrayList<>();
        if(dcLink.getConfig().getDiscordConfiguration() != null){
            Guild guild = jda.getGuildById(dcLink.getConfig().getDiscordConfiguration().getGuild());
            if(guild != null){
                Member member = guild.retrieveMemberById(this.discordId).complete();
                if(member != null){
                    member.getRoles().forEach(role -> roleList.add(new DiscordRoleImpl(jda, role.getId()))
                    );
                }
            }
        }
        return roleList;
    }

    @Override
    public boolean addRole(DiscordRole apiRole) {
        if(this.discordId == null) {
            return false;
        }
        Guild guild = jda.getGuildById(dcLink.getConfig().getDiscordConfiguration().getGuild());
        if(guild != null){
            Role role = guild.getRoleById(apiRole.getId());
            if(role != null){
                Member member = guild.retrieveMemberById(this.discordId).complete();
                guild.addRoleToMember(member, role).complete();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeRole(DiscordRole apiRole) {
        if(this.discordId == null) {
            return false;
        }
        Guild guild = jda.getGuildById(dcLink.getConfig().getDiscordConfiguration().getGuild());
        if(guild != null){
            Role role = guild.getRoleById(apiRole.getId());
            if(role != null){
                Member member = guild.retrieveMemberById(this.discordId).complete();
                guild.removeRoleFromMember(member, role).complete();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMemberOfGuild() {
        if(this.discordId == null) {
            return false;
        }

        Guild guild = jda.getGuildById(dcLink.getConfig().getDiscordConfiguration().getGuild());
        if(guild != null){
            Member member = guild.retrieveMemberById(this.discordId).complete();
            return member != null;
        }
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
