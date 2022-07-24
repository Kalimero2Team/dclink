package com.kalimero2.team.dclink.fabric.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.fabric.FabricServerCommandManager;
import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlatformCommands;
import com.kalimero2.team.dclink.fabric.FabricDCLink;

public class FabricCommands implements PlatformCommands {
    private final FabricDCLink dcLink;

    public FabricCommands(final FabricDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Commander> createCommandManager() {
        final FabricServerCommandManager<Commander> mgr = new FabricServerCommandManager<>(
                CommandExecutionCoordinator.simpleCoordinator(),
                FabricCommander::from,
                commander -> ((FabricCommander) commander).stack()
        );

        BrigadierSetup.setup(mgr);

        return mgr;
    }

}
