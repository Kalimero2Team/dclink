package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.DCLinkConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
public class DiscordBot {

    private final Logger logger = LoggerFactory.getLogger("dclink-discord");
    private final JDA jda;
    private final DCLinkConfig.DiscordConfiguration discordConfiguration;

    public DiscordBot(DCLink dcLink) throws LoginException, InterruptedException {
        this.discordConfiguration = dcLink.getConfig().discordConfiguration;
        JDABuilder builder = JDABuilder.createDefault(discordConfiguration.getToken());
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        builder.setLargeThreshold(50);

        builder.setActivity(Activity.playing(discordConfiguration.getStatusMessage()));

        jda = builder.build();

        jda.awaitReady();

        new BotCommands(dcLink, jda, discordConfiguration.getGuild());
    }




}
