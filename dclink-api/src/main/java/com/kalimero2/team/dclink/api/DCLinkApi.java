package com.kalimero2.team.dclink.api;

import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.discord.DiscordRole;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.api.game.GameType;

import java.util.UUID;

public interface DCLinkApi {

    static DCLinkApi getApi() {
        return DCLinkApiHolder.getApi();
    }

    /**
     * Gets the type of game DCLink is running on
     * @return GameType
     */
    GameType getGameType();

    /**
     * Get the GamePlayer with the given UUID.
     *
     * @return GamePlayer with the given UUID.
     */
    GamePlayer getGamePlayer(UUID uuid);

    /**
     * Get the DiscordAccount with the given ID.
     *
     * @return DiscordAccount with the given ID.
     */
    DiscordAccount getDiscordAccount(String id);

    /**
     * Get the DiscordRole with the given ID.
     *
     * @return DiscordRole with the given ID.
     */
    DiscordRole getDiscordRole(String id);

    boolean linkAccounts(GamePlayer gamePlayer, DiscordAccount discordAccount);

    void unLinkAccounts(DiscordAccount discordAccount);

    void unLinkAccount(GamePlayer gamePlayer);

}
