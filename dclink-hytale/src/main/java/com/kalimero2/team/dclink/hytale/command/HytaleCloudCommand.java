package com.kalimero2.team.dclink.hytale.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
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
    public String getDescription() {
        return component.description().textDescription();
    }

    @Override
    public @NotNull Set<String> getAliases() {
        return Set.copyOf(component.aliases());
    }

    @Override
    public @NotNull Message getUsageShort(@NotNull CommandSender sender, boolean fullyQualify) {
        C cloudSender = commandManager.getSenderMapper().map(sender);
        String syntax = commandManager.commandSyntaxFormatter().apply(cloudSender, List.of(component), null);
        return Message.raw((fullyQualify ? "/" : "") + syntax + " ...");
    }

    @Override
    public @NotNull Message getUsageString(@NotNull CommandSender sender) {
        C cloudSender = commandManager.getSenderMapper().map(sender);
        String syntax = commandManager.commandSyntaxFormatter().apply(cloudSender, List.of(component), null);
        return Message.raw("/" + syntax + " ...");
    }

    @Override
    public @Nullable String getPermission() {
        for (Command<C> command : commandManager.commands()) {
            if (command.rootComponent().equals(component)) {
                if (command.commandPermission() instanceof Permission permission) {
                    return permission.permissionString();
                }
            }
        }
        return null;
    }

    @Override
    protected CompletableFuture<Void> execute(CommandContext context) {
        String input = context.getInputString();

        return commandManager.commandExecutor().executeCommand(
                commandManager.getSenderMapper().map(context.sender()),
                input
        ).handle((result, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof java.util.concurrent.CompletionException) {
                    throwable = throwable.getCause();
                }
                if (throwable instanceof InvalidSyntaxException syntaxException) {
                    context.sender().sendMessage(Message.raw("Invalid command syntax. Correct syntax is: /" + syntaxException.correctSyntax()).color(NamedTextColor.RED.asHexString()));
                } else if (throwable instanceof NoPermissionException) {
                    context.sender().sendMessage(Message.raw("I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.").color(NamedTextColor.RED.asHexString()));
                } else if (throwable instanceof ArgumentParseException argumentParseException) {
                    context.sender().sendMessage(Message.raw("Invalid command argument: " + argumentParseException.getCause().getMessage()).color(NamedTextColor.RED.asHexString()));
                } else {
                    context.sender().sendMessage(Message.raw("An internal error occurred while attempting to perform this command.").color(NamedTextColor.RED.asHexString()));
                    throwable.printStackTrace();
                }
            }
            return null;
        });
    }
}
