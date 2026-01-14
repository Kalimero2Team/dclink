package com.kalimero2.team.dclink.hytale.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.exception.InvalidSyntaxException;

import java.util.concurrent.CompletableFuture;

public class HytaleCloudCommand<C> extends AbstractCommand {

    private final HytaleCommandManager<C> commandManager;
    private final CommandComponent<C> component;

    public HytaleCloudCommand(HytaleCommandManager<C> commandManager, CommandComponent<C> component) {
        super(component.name());
        this.commandManager = commandManager;
        this.component = component;

        this.setAllowsExtraArguments(true);
    }

    @Override
    public String getName() {
        return component.name();
    }

    @Override
    protected CompletableFuture<Void> execute(CommandContext context) {
        String input = context.getInputString();

        try {
            return commandManager.commandExecutor().executeCommand(
                    commandManager.getSenderMapper().map(context.sender()),
                    input
            ).thenApply(r -> null);
        }catch (InvalidSyntaxException e){
            context.sender().sendMessage(Message.raw(e.getMessage()));
            return CompletableFuture.completedFuture(null);
        }
    }
}
