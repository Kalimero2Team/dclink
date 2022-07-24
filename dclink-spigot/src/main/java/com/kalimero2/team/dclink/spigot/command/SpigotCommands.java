package com.kalimero2.team.dclink.spigot.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlatformCommands;
import com.kalimero2.team.dclink.spigot.SpigotDCLink;

public class SpigotCommands implements PlatformCommands {
    private final SpigotDCLink dcLink;

    public SpigotCommands(final SpigotDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Commander> createCommandManager() {
        final BukkitCommandManager<Commander> commandManager;
        try {
            commandManager = new BukkitCommandManager<>(
                    this.dcLink.getPlugin(),
                    CommandExecutionCoordinator.simpleCoordinator(),
                    SpigotCommander::from,
                    commander -> ((SpigotCommander) commander).sender()
            );
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to initialize command manager", ex);
        }

        commandManager.registerBrigadier();
        BrigadierSetup.setup(commandManager);

        return commandManager;
    }

}
