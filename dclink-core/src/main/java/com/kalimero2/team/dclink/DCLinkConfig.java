package com.kalimero2.team.dclink;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;

public class DCLinkConfig {

    private final HoconConfigurationLoader loader;
    private final CommentedConfigurationNode node;
    public final DatabaseConfiguration databaseConfiguration;
    public final DiscordConfiguration discordConfiguration;
    public final LinkingConfiguration linkingConfiguration;


    public DCLinkConfig(String configPath) throws ConfigurateException {
        File config = new File(configPath);
        loader = HoconConfigurationLoader.builder()
                .defaultOptions(ConfigurationOptions.defaults().shouldCopyDefaults(true))
                .file(config)
                .build();
        if(!config.exists()){
            node = CommentedConfigurationNode.root();
        }else {
            node = loader.load();
        }

        databaseConfiguration = node.node("database").get(DatabaseConfiguration.class);
        discordConfiguration = node.node("discord").get(DiscordConfiguration.class);
        linkingConfiguration = node.node("linking").get(LinkingConfiguration.class);

        if(!config.exists()){
            save();
        }
    }

    public void save() throws ConfigurateException {
        node.node("database").set(DatabaseConfiguration.class, databaseConfiguration);
        node.node("discord").set(DiscordConfiguration.class, discordConfiguration);
        node.node("linking").set(LinkingConfiguration.class, linkingConfiguration);
        loader.save(node);
    }

    @ConfigSerializable
    public static class DatabaseConfiguration{
        @Comment("The Sqlite database filename")
        public String sqliteFile = "dclink.db";


        /* Future Options when multiple database types are supported
        @Comment("The JDBC URL to use for the database (e.g. jdbc:mysql://localhost:3306/dclink)")
        private String jdbcUrl = "";
        @Comment("The username to use for the database (Can be left blank if the database (type) isn't requiring authentication)")
        private String username = "";
        @Comment("The password to use for the database (Can be left blank if the database (type) isn't requiring authentication)")
        private String password = "";

        public String getJdbcUrl() {
            return jdbcUrl;
        }
        public String getUserName() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        */
    }

    @ConfigSerializable
    public static class DiscordConfiguration {
        @Comment("Bot Token (see https://discord.com/developers/applications)")
        public String token = "";
        @Comment("Guild ID of the Guild where the bot will run")
        public String guild = "";
        @Comment("Channel ID of the channel where the bot will send the message with the button to link their account")
        public String linkChannel = "";
        @Comment("Role ID of the role that the bot will give to the linked players (If left blank, the bot will not give any roles)")
        public @Nullable String linkRole = "";
        @Comment("Message to show on the bot's status")
        public String statusMessage = "Minecraft";
    }

    @ConfigSerializable
    public static class LinkingConfiguration{
        @Comment("If true, the player needs to be linked before they can join the server")
        public boolean linkRequired = true;
        @Comment("Limit of Java Edition accounts that can be linked to one Discord account")
        public int javaLimit = 1;
        @Comment("Limit of Bedrock Edition accounts that can be linked to one Discord account. Requires Floodgate to be installed")
        public int bedrockLimit = 1;
    }

}