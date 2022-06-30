package com.kalimero2.team.dclink;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.io.File;

public class DCLinkMessages {

    private final HoconConfigurationLoader loader;
    private final CommentedConfigurationNode node;
    public final DiscordBotMessages discordBotMessages;

    public DCLinkMessages(String configPath) throws ConfigurateException {
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

        discordBotMessages = node.node("discord").get(DiscordBotMessages.class);

        if(!config.exists()){
            save();
        }
    }

    public void save() throws ConfigurateException {
        node.node("discord").set(DiscordBotMessages.class, discordBotMessages);
        loader.save(node);
    }

    @ConfigSerializable
    public static class DiscordBotMessages{
        @Comment("Button Descriptions")
        public String add = "Click to add yourself to the whitelist";
        public String accept = "Accept";
        public String decline = "Decline";
        public String cancel = "Cancel";
        @Comment("Bot Messages")
        public String infoChannel = "Press the button below to link your account";
        public String trustChannel = "Send your code in this channel";
        public String wrongCode = "The code you entered is incorrect";
        public String maxBedrock = "You can't link any more Java accounts.";
        public String maxJava = "You can't link any more Java accounts.";

        public String rules = "To join the server you have to accept these rules";
        public String rulesAccepted = "You have accepted the rules. You can now join the server.";
        public String rulesDenied = "You have denied the rules.";

    }

}
