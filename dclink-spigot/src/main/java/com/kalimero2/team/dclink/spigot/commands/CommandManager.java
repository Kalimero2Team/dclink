package com.kalimero2.team.dclink.spigot.commands;

import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.google.common.collect.ImmutableList;
import com.kalimero2.team.dclink.spigot.SpigotDCLink;
import org.bukkit.command.CommandSender;

import java.util.function.UnaryOperator;

public class CommandManager extends BukkitCommandManager<CommandSender> {

    public CommandManager(final SpigotDCLink dcLink) throws Exception {
        super(
                dcLink.getPlugin(),
                CommandExecutionCoordinator.simpleCoordinator(),
                UnaryOperator.identity(),
                UnaryOperator.identity()
        );

        if (this.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            try {
                this.registerBrigadier();
                final CloudBrigadierManager<?, ?> brigManager = this.brigadierManager();
                if (brigManager != null) {
                    brigManager.setNativeNumberSuggestions(false);
                }
            } catch (final Exception e) {
                dcLink.getLogger().warn("Failed to initialize Brigadier support: " + e.getMessage());
            }
        }


        ImmutableList.of(
            new DiscordCommand(dcLink, this),
            new AltsCommand(dcLink, this),
            new LinkCommand(dcLink, this),
            new UnLinkCommand(dcLink, this)
        ).forEach(CommandHandler::register);


    }


}
