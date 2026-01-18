package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.DCLinkConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.time.Duration;

public class DiscordBot {

    private final Logger logger = LoggerFactory.getLogger("dclink-discord");
    private final DCLink dcLink;
    private final JDA jda;
    private final DCLinkConfig.DiscordConfiguration discordConfiguration;

    public DiscordBot(DCLink dcLink) throws LoginException, InterruptedException {
        this.dcLink = dcLink;
        this.discordConfiguration = dcLink.getConfig().getDiscordConfiguration();
        JDALogger.setFallbackLoggerEnabled(false);
        String token = discordConfiguration.getToken();

        if (token.isEmpty()) {
            logger.error("No token found in config");
            throw new LoginException("No token found in config");
        }

        JDABuilder builder = JDABuilder.createLight(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setLargeThreshold(50);

        builder.setActivity(Activity.playing(discordConfiguration.getStatusMessage()));

        jda = builder.build();

        jda.awaitReady();

    }

    public void loadFeatures() {
        new BotCommands(dcLink, jda, discordConfiguration.getGuild());
        new DiscordAccountLinker(dcLink, jda);
    }

    public void shutdown() {
        jda.shutdownNow();
        try {
            boolean awaited = jda.awaitShutdown(Duration.ofSeconds(5));
            if (!awaited) {
                logger.error("Failed to shutdown discord bot in 5 seconds, forcing shutdown");
            }
        } catch (InterruptedException e) {
            logger.error("Failed to shutdown discord bot", e);
        }
    }

    public JDA getJda() {
        return jda;
    }

    public Logger getLogger() {
        return logger;
    }
}
