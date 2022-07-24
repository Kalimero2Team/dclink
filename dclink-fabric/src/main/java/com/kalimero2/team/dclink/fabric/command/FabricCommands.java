package com.kalimero2.team.dclink.fabric.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.fabric.FabricServerCommandManager;
import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlatformCommands;

public class FabricCommands implements PlatformCommands {

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

}
