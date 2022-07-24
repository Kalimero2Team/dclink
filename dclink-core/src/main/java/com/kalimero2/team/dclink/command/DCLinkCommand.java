package com.kalimero2.team.dclink.command;

import com.kalimero2.team.dclink.DCLink;

public abstract class DCLinkCommand {
    protected final Commands commands;
    protected final DCLink dcLink;

    protected DCLinkCommand(final Commands commands) {
        this.commands = commands;
        this.dcLink = commands.getDCLink();
    }

    public abstract void register();


}
