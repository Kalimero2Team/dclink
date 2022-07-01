package com.kalimero2.team.dclink.paper.commands;

import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.kalimero2.team.dclink.paper.PaperDCLink;
import org.bukkit.command.CommandSender;

import java.util.function.UnaryOperator;

public class CommandManager extends PaperCommandManager<CommandSender> {


    public CommandManager(final PaperDCLink dcLink) throws Exception {
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

        if (this.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.registerAsynchronousCompletions();
        }


        ImmutableList.of(
            new DiscordCommand(dcLink, this),
            new AltsCommand(dcLink, this),
            new LinkCommand(dcLink, this),
            new UnLinkCommand(dcLink, this)
        ).forEach(CommandHandler::register);


    }


}
