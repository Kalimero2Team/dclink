package com.kalimero2.team.dclink.api.minecraft;

import com.kalimero2.team.dclink.api.discord.DiscordAccount;

import java.util.UUID;

public interface MinecraftPlayer {

    /**
     * Get the Minecraft player's Name.
     *
     * @return The Minecraft player's Name.
     */
    String getName();

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
