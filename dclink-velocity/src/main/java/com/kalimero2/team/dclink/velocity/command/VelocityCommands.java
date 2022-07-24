package com.kalimero2.team.dclink.velocity.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.velocity.VelocityCommandManager;
import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlatformCommands;
import com.kalimero2.team.dclink.velocity.VelocityDCLink;

public class VelocityCommands implements PlatformCommands {
    private final VelocityDCLink dcLink;

    public VelocityCommands(final VelocityDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Commander> createCommandManager() {
        final VelocityCommandManager<Commander> commandManager;
        try {
            commandManager = new VelocityCommandManager<>(
                    dcLink.getServer().getPluginManager().getPlugin("dclink-velocity").orElse(null),
                    this.dcLink.getServer(),
                    CommandExecutionCoordinator.simpleCoordinator(),
                    VelocityCommander::from,
                    commander -> ((VelocityCommander) commander).source()
            );
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to initialize command manager", ex);
        }

        BrigadierSetup.setup(commandManager);

        return commandManager;
    }

}
