package com.kalimero2.team.dclink.api.minecraft;

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

}
