package com.kalimero2.team.dclink.api.game;

import com.kalimero2.team.dclink.api.discord.DiscordAccount;

import java.util.UUID;

public interface GamePlayer {

    /**
     * Get the Minecraft player's Name.
     *
     * @return The Minecraft player's Name.
     */
    String getName();

    /**
     * Set the Minecraft player's Name.
     *
     * @param name The Minecraft player's Name.
     */
    void setName(String name);

    /**
     * Get the Minecraft player's UUID.
     *
     * @return The Minecraft player's UUID.
     */
    UUID getUuid();

    /**
     * Check if the Minecraft player is linked to a Discord account.
     *
     * @return true if the player is linked to a Discord account.
     */
    boolean isLinked();

    /**
     * Get the linked Discord Account. Returns null if there isn't any account linked
     *
     * @return Discord Account if present. Null if there isn't any account linked.
     * @see com.kalimero2.team.dclink.api.discord.DiscordAccount
     */
    DiscordAccount getDiscordAccount();

}
