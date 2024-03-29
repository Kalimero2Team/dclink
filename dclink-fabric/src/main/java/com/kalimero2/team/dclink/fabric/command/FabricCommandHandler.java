package com.kalimero2.team.dclink.fabric.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.fabric.FabricServerCommandManager;
import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.PlatformHandler;
import com.kalimero2.team.dclink.fabric.FabricDCLink;

import java.util.ArrayList;
import java.util.List;

public class FabricCommandHandler implements PlatformHandler {
    private final FabricDCLink dcLink;

    public FabricCommandHandler(final FabricDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Sender> createCommandManager() {
        final FabricServerCommandManager<Sender> commandManager = new FabricServerCommandManager<>(
                CommandExecutionCoordinator.simpleCoordinator(),
                FabricSender::from,
                sender -> ((FabricSender) sender).stack()
        );

        BrigadierSetup.setup(commandManager);


        return commandManager;
    }

    @Override
    public <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext) {
        List<String> output = new ArrayList<>();
        dcLink.getServer().getPlayerList().getPlayers().forEach(player -> output.add(player.getName().getString()));
        return output;
    }
}
