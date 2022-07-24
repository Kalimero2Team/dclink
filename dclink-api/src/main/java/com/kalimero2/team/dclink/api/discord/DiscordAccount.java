package com.kalimero2.team.dclink.api.discord;

import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.Collection;

public interface DiscordAccount {

    /**
     * Get the Discord Name of the account.
     *
     * @return The Discord Name of the account.
     */
    String getName();

    /**
     * Get the Discord Discriminator of the account.
     *
     * @return The Discord Discriminator of the account.
     */
    String getDiscriminator();

    /**
     * Get the Discord ID of the account.
     *
     * @return The Discord ID of the account.
     */
    String getId();

    /**
     * Get the Discord Roles of the account.
     *
     * @return The Discord Roles of the account.
     */
    Collection<DiscordRole> getRoles();

    /**
     * Add a Discord Role to the account.
     *
     * @return Whether the role was added.
     */
    boolean addRole(DiscordRole role);

    /**
     * Remove a Discord Role to the account.
     *
     * @return Whether the role was removed.
     */
    boolean removeRole(DiscordRole role);

    /**
     * Check if the Discord account is a Member of the Guild.
     *
     * @return true if the account is a Member of the Guild.
     */
    boolean isMemberOfGuild();

    /**
     * Get the Minecraft Players linked to the account.
     *
     * @return The Minecraft Players linked to the account.
     * @see com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer
     */
    Collection<MinecraftPlayer> getLinkedPlayers();
}
