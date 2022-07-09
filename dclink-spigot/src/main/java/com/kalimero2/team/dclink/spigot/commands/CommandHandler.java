package com.kalimero2.team.dclink.spigot.commands;

import com.kalimero2.team.dclink.spigot.SpigotDCLink;

public abstract class CommandHandler {
    protected final SpigotDCLink dcLink;
    protected final CommandManager commandManager;

    protected CommandHandler(
            final SpigotDCLink dcLink,
            final CommandManager commandManager
    ) {
        this.dcLink = dcLink;
        this.commandManager = commandManager;
    }

    public abstract void register();
}
