package com.kalimero2.team.dclink;

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

        if(!config.exists()){
            save();
        }
    }

    public void save() throws ConfigurateException {
        node.node("database").set(DatabaseConfiguration.class, databaseConfiguration);
        node.node("discord").set(DiscordConfiguration.class, discordConfiguration);
        loader.save(node);
    }

    @ConfigSerializable
    static class DatabaseConfiguration{
        @Comment("The JDBC URL to use for the database (e.g. jdbc:mysql://localhost:3306/dclink)")
        private String jdbcUrl = "";
        @Comment("The username to use for the database (Can be left blank if the database (type) isn't requiring authentication)")
        private String user = "";
        @Comment("The password to use for the database (Can be left blank if the database (type) isn't requiring authentication)")
        private String password = "";

        public String getJdbcUrl() {
            return jdbcUrl;
        }
        public String getUser() {
            return user;
        }
        public String getPassword() {
            return password;
        }
    }

    @ConfigSerializable
    static class DiscordConfiguration{
        @Comment("Bot Token (see https://discord.com/developers/applications)")
        private String token = "";
        @Comment("Guild ID of the Guild where the bot will run")
        private String guild = "";
        @Comment("Channel ID of the channel where the bot will send the message with the button to link their account")
        private String linkChannel = "";
        @Comment("Category ID of the category where the bot will create the link channels")
        private String linkCategory = "";

        public String getToken() {
            return token;
        }
        public String getGuild() {
            return guild;
        }
        public String getLinkChannel() {
            return linkChannel;
        }
        public String getLinkCategory() {
            return linkCategory;
        }
    }

}
