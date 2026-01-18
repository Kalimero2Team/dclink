package com.kalimero2.team.dclink;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DCLinkConfig {

    private final HoconConfigurationLoader loader;
    private final CommentedConfigurationNode node;
    private final DatabaseConfiguration databaseConfiguration;
    private final DiscordConfiguration discordConfiguration;
    private final LinkingConfiguration linkingConfiguration;


    public DCLinkConfig(String configPath) throws ConfigurateException {
        File config = new File(configPath);
        loader = HoconConfigurationLoader.builder()
                .defaultOptions(ConfigurationOptions.defaults().shouldCopyDefaults(true))
                .file(config)
                .build();
        if (!config.exists()) {
            node = CommentedConfigurationNode.root();
        } else {
            node = loader.load();
        }

        databaseConfiguration = node.node("database").get(DatabaseConfiguration.class);
        discordConfiguration = node.node("discord").get(DiscordConfiguration.class);
        linkingConfiguration = node.node("linking").get(LinkingConfiguration.class);

        save();
    }

    public void save() throws ConfigurateException {
        node.node("database").set(DatabaseConfiguration.class, databaseConfiguration);
        node.node("discord").set(DiscordConfiguration.class, discordConfiguration);
        node.node("linking").set(LinkingConfiguration.class, linkingConfiguration);
        loader.save(node);
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public DiscordConfiguration getDiscordConfiguration() {
        return discordConfiguration;
    }

    public LinkingConfiguration getLinkingConfiguration() {
        return linkingConfiguration;
    }

    @ConfigSerializable
    public static class DatabaseConfiguration {
        @Comment("The Sqlite database filename")
        private String sqliteFile = "dclink.db";

        public String getSqliteFile() {
            return sqliteFile;
        }

        public void setSqliteFile(String sqliteFile) {
            this.sqliteFile = sqliteFile;
        }
    }

    @ConfigSerializable
    public static class DiscordConfiguration {
        @Comment("Bot Token (see https://discord.com/developers/applications)")
        private String token = "";
        @Comment("Guild ID of the Guild where the bot will run")
        private String guild = "";
        @Comment("Channel ID of the channel where the bot will send the message with the button to link their account")
        private String linkChannel = "";
        @Comment("Role ID of the role that the bot will give to the linked players (If left blank, the bot will not give any roles)")
        private @Nullable String linkRole = "";
        @Comment("List of Role IDs that are required to link an account (If empty, no roles are required)")
        private List<String> requiredRoles = new ArrayList<>();
        @Comment("Message to show on the bot's status (Examples: Hytale, Minecraft)")
        private String statusMessage = "Minecraft";
        @Comment("If true, the bot will send the link message as an embed")
        private boolean useEmbed = false;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getGuild() {
            return guild;
        }

        public void setGuild(String guild) {
            this.guild = guild;
        }

        public String getLinkChannel() {
            return linkChannel;
        }

        public void setLinkChannel(String linkChannel) {
            this.linkChannel = linkChannel;
        }

        @Nullable
        public String getLinkRole() {
            return linkRole;
        }

        public void setLinkRole(String linkRole) {
            this.linkRole = linkRole;
        }

        public List<String> getRequiredRoles() {
            return requiredRoles;
        }

        public void setRequiredRoles(List<String> requiredRoles) {
            this.requiredRoles = requiredRoles;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        public boolean isUseEmbed() {
            return useEmbed;
        }

        public void setUseEmbed(boolean useEmbed) {
            this.useEmbed = useEmbed;
        }
    }

    @ConfigSerializable
    public static class LinkingConfiguration {
        @Comment("If true, the player needs to be linked before they can join the server")
        private boolean linkRequired = true;
        @Comment("If true, the bot will validate required roles and guild membership on every join instead of just relying on the events.")
        private boolean validateOnJoin = false;
        @Comment("Limit of Minecraft Java Edition or Hytale accounts that can be linked to one Discord account")
        private int javaLimit = 1;
        @Comment("(Minecraft ONLY) Limit of Bedrock Edition accounts that can be linked to one Discord account. Requires Floodgate to be installed")
        private int bedrockLimit = 1;

        public boolean isLinkRequired() {
            return linkRequired;
        }

        public void setLinkRequired(boolean linkRequired) {
            this.linkRequired = linkRequired;
        }

        public boolean getValidateOnJoin() {
            return validateOnJoin;
        }

        public void setValidateOnJoin(boolean validateOnJoin) {
            this.validateOnJoin = validateOnJoin;
        }

        public int getJavaLimit() {
            return javaLimit;
        }

        public void setJavaLimit(int javaLimit) {
            this.javaLimit = javaLimit;
        }

        public int getBedrockLimit() {
            return bedrockLimit;
        }

        public void setBedrockLimit(int bedrockLimit) {
            this.bedrockLimit = bedrockLimit;
        }
    }
}