package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class DiscordBot {

    private final DCLink dcLink;
    private final JDA jda;

    public DiscordBot(DCLink dcLink, String token) throws LoginException, InterruptedException {
        this.dcLink = dcLink;
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        builder.setLargeThreshold(50);
        builder.setActivity(Activity.playing("Minecraft"));

        jda = builder.build();

        jda.awaitReady();
    }



}
