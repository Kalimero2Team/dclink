package com.kalimero2.team.dclink;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;

public class DCLinkMessages {

    private final HoconConfigurationLoader loader;
    private final CommentedConfigurationNode node;
    public final Messages messages;

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

        messages = node.get(Messages.class);

        if(!config.exists()){
            save();
        }
    }

    public void save() throws ConfigurateException {
        node.set(DCLinkMessages.class, messages);
        loader.save(node);
    }

    @ConfigSerializable
    static class Messages{
        private String discordActivity = "Minecraft";

    }


}
