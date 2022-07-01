package com.kalimero2.team.dclink.paper.commands;

import com.kalimero2.team.dclink.paper.PaperDCLink;

public abstract class CommandHandler {
    protected final PaperDCLink dcLink;
    protected final CommandManager commandManager;

    protected CommandHandler(
            final PaperDCLink dcLink,
            final CommandManager commandManager
    ) {
        this.dcLink = dcLink;
        this.commandManager = commandManager;
    }

    public abstract void register();
}
