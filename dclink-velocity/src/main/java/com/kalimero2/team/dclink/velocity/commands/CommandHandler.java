package com.kalimero2.team.dclink.velocity.commands;

import com.kalimero2.team.dclink.velocity.VelocityDCLink;

public abstract class CommandHandler {
    protected final VelocityDCLink dcLink;
    protected final CommandManager commandManager;

    protected CommandHandler(
            final VelocityDCLink dcLink,
            final CommandManager commandManager
    ) {
        this.dcLink = dcLink;
        this.commandManager = commandManager;
    }

    public abstract void register();
}
