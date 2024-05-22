package com.kalimero2.team.dclink.command;


import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

public interface PlatformHandler {
    CommandManager<Sender> createCommandManager();


    <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext);
}
