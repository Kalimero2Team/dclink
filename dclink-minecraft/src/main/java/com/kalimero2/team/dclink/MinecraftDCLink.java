package com.kalimero2.team.dclink;

import com.kalimero2.team.dclink.api.game.GameType;

public abstract class MinecraftDCLink extends DCLink {

    @Override
    public GameType getGameType() {
        return GameType.MINECRAFT;
    }

}
