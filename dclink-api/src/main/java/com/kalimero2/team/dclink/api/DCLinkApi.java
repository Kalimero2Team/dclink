package com.kalimero2.team.dclink.api;

import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.UUID;

public interface DCLinkApi {

    static DCLinkApi getApi() {
        return DCLinkApiHolder.getApi();
    }

    /**
     * Get the MinecraftPlayer with the given UUID.
     *
     * @return MinecraftPlayer with the given UUID.
     */
    MinecraftPlayer getMinecraftPlayer(UUID uuid);

    /**
     * Get the DiscordAccount with the given ID.
     *
     * @return DiscordAccount with the given ID.
     */
    DiscordAccount getDiscordAccount(String id);

    boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount);

}
