package com.kalimero2.team.dclink;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;

public class DCLinkMessages {

    private final HoconConfigurationLoader loader;
    private final CommentedConfigurationNode node;
    private final DiscordBotMessages discordBotMessages;
    private final MinecraftMessages minecraftMessages;

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
        minecraftMessages = node.node("minecraft").get(MinecraftMessages.class);

        if(!config.exists()){
            save();
        }
    }

    public void save() throws ConfigurateException {
        node.node("discord").set(DiscordBotMessages.class, discordBotMessages);
        node.node("minecraft").set(MinecraftMessages.class, minecraftMessages);
        loader.save(node);
    }

    @ConfigSerializable
    public static class DiscordBotMessages{
        public String add = "Link Account";
        public String accept = "Accept";
        public String decline = "Decline";
        public String infoChannel = "Press the button below to link your account.";
        public String wrongCode = "The code you entered is incorrect.";
        public String maxBedrock = "You can't link any more Bedrock accounts.";
        public String maxJava = "You can't link any more Java accounts.";
        public String alreadyLinked = "You are already linked to this account.";

        public String rules = "Accept the following rules in order to link your account!\nRules: \nRule1\nRule2\nRule3";
        public String rulesAccepted = "You have accepted the rules! Your account is now linked.";
        public String rulesDenied = "You have denied the rules.";
        public String genericLinkError = "An error occurred while linking your accounts. Contact the server administrator if this problem persists.";

        public String modalTitle = "Account Linker";
        public String modalInputDescription = "Enter link code";
    }

    @ConfigSerializable
    public static class MinecraftMessages{
        public String altsCommand = "Alts: <alts>";
        public String discordCommand = "Name: <hover:show_text:\"ID: <discord_id>\"><discord_name>";
        public String notLinked = "Not Linked";
        public String unLinkCommand = "Unlinked <player>";
        public String linkCodeMessage = "Your Linking Code is: <code>";
        public String kickUnlinked = "You have been kicked because your Discord account is no longer linked.";
        public String needsArgumentIfExecutedByConsole = "This command needs an argument if executed by console.";
    }

    public Component getMinifiedMessage(String message, TagResolver... tagResolvers){
        return MiniMessage.miniMessage().deserialize(message, tagResolvers);
    }

    public DiscordBotMessages getDiscordBotMessages() {
        return discordBotMessages;
    }

    public MinecraftMessages getMinecraftMessages() {
        return minecraftMessages;
    }
}
