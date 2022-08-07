package com.kalimero2.team.dclink.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;

import java.util.List;

public interface PlatformCommands {
    CommandManager<Commander> createCommandManager();

    <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext);
}
