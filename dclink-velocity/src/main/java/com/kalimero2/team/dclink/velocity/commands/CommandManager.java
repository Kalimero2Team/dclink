package com.kalimero2.team.dclink.velocity.commands;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.velocity.VelocityCommandManager;
import com.google.common.collect.ImmutableList;
import com.kalimero2.team.dclink.velocity.VelocityDCLink;
import com.velocitypowered.api.command.CommandSource;

import java.util.function.UnaryOperator;

public class CommandManager extends VelocityCommandManager<CommandSource> {


    public CommandManager(final VelocityDCLink dcLink){
        super(
                dcLink.getServer().getPluginManager().getPlugin("dclink-velocity").orElse(null),
                dcLink.getServer(),
                CommandExecutionCoordinator.simpleCoordinator(),
                UnaryOperator.identity(),
                UnaryOperator.identity()
        );

        ImmutableList.of(
            new DiscordCommand(dcLink, this),
            new AltsCommand(dcLink, this),
            new LinkCommand(dcLink, this),
            new UnLinkCommand(dcLink, this)
        ).forEach(CommandHandler::register);


    }


}
