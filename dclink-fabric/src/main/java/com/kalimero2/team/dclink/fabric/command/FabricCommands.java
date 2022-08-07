package com.kalimero2.team.dclink.fabric.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.fabric.FabricServerCommandManager;
import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlatformCommands;
import com.kalimero2.team.dclink.fabric.FabricDCLink;

import java.util.ArrayList;
import java.util.List;

public class FabricCommands implements PlatformCommands {
    private final FabricDCLink dcLink;

    public FabricCommands(final FabricDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Commander> createCommandManager() {
        final FabricServerCommandManager<Commander> commandManager = new FabricServerCommandManager<>(
                CommandExecutionCoordinator.simpleCoordinator(),
                FabricCommander::from,
                commander -> ((FabricCommander) commander).stack()
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
