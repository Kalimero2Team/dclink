package com.kalimero2.team.dclink.command;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.command.commands.AltsCommand;
import com.kalimero2.team.dclink.command.commands.DiscordCommand;
import com.kalimero2.team.dclink.command.commands.LinkCommand;
import com.kalimero2.team.dclink.command.commands.UnLinkCommand;
import io.leangen.geantyref.TypeToken;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;

import java.util.List;

public class Commands {

    public static final CloudKey<DCLink> DCLINK = createTypeKey(DCLink.class);
    public static final CloudKey<PlatformHandler> PLATFORMCOMMANDS = createTypeKey(PlatformHandler.class);
    private final CommandManager<Sender> commandManager;
    private final DCLink dcLink;

    public Commands(DCLink dcLink, PlatformHandler platformCommands) {
        this.dcLink = dcLink;
        this.commandManager = platformCommands.createCommandManager();

        this.commandManager.registerCommandPreProcessor(preprocessContext -> {
            final CommandContext<Sender> commandContext = preprocessContext.commandContext();
            commandContext.store(DCLINK, dcLink);
            commandContext.store(PLATFORMCOMMANDS, platformCommands);
        });
    }

    private static <T> CloudKey<T> createTypeKey(final Class<T> type) {
        return CloudKey.of("dclink-" + type.getName(), TypeToken.get(type));
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

    public CommandManager<Sender> commandManager() {
        return this.commandManager;
    }

    public DCLink getDCLink() {
        return dcLink;
    }
}
