package com.kalimero2.team.dclink.paper.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlatformCommands;
import com.kalimero2.team.dclink.paper.PaperDCLink;

public class PaperCommands implements PlatformCommands {
    private final PaperDCLink dcLink;

    public PaperCommands(final PaperDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Commander> createCommandManager() {
        final PaperCommandManager<Commander> commandManager;
        try {
            commandManager = new PaperCommandManager<>(
                    this.dcLink.getPlugin(),
                    CommandExecutionCoordinator.simpleCoordinator(),
                    PaperCommander::from,
                    commander -> ((PaperCommander) commander).sender()
            );
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to initialize command manager", ex);
        }

        commandManager.registerBrigadier();
        BrigadierSetup.setup(commandManager);
        commandManager.registerAsynchronousCompletions();

        return commandManager;
    }

}
