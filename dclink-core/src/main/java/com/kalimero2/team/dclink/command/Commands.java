package com.kalimero2.team.dclink.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.command.commands.AltsCommand;
import com.kalimero2.team.dclink.command.commands.DiscordCommand;
import com.kalimero2.team.dclink.command.commands.LinkCommand;
import com.kalimero2.team.dclink.command.commands.UnLinkCommand;
import io.leangen.geantyref.TypeToken;

import java.util.List;

public class Commands {

    public static final CloudKey<DCLink> DCLINK = createTypeKey(DCLink.class);
    private final CommandManager<Commander> commandManager;
    private final DCLink dcLink;

    public Commands(DCLink dcLink, PlatformCommands platformCommands){
        this.dcLink = dcLink;
        this.commandManager = platformCommands.createCommandManager();

        this.commandManager.registerCommandPreProcessor(preprocessContext -> {
            final CommandContext<Commander> commandContext = preprocessContext.getCommandContext();
            commandContext.store(DCLINK, dcLink);
        });
    }

    public void registerCommands() {
        List<DCLinkCommand> commands = List.of(
                new AltsCommand(this),
                new DiscordCommand(this),
                new LinkCommand(this),
                new UnLinkCommand(this)
        );

        commands.forEach(DCLinkCommand::register);

    }

    public CommandManager<Commander> commandManager() {
        return this.commandManager;
    }

    public DCLink getDCLink() {
        return dcLink;
    }

    private static <T> CloudKey<T> createTypeKey(final Class<T> type) {
        return SimpleCloudKey.of("dclink-" + type.getName(), TypeToken.get(type));
    }
}
