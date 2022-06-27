package com.kalimero2.team.dclink;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;

import java.util.UUID;

public abstract class DCLink implements DCLinkApi {

    public abstract void load();

    public abstract void enable();

    public abstract void disable();

    @Override
    public MinecraftPlayer getMinecraftPlayer(UUID uuid) {
        return null;
    }

    @Override
    public DiscordAccount getDiscordMember(String id) {
        return null;
    }

    @Override
    public boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount) {
        return false;
    }
}
