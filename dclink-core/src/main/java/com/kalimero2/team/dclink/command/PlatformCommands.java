package com.kalimero2.team.dclink.command;

import cloud.commandframework.CommandManager;

public interface PlatformCommands {
    CommandManager<Commander> createCommandManager();
}
