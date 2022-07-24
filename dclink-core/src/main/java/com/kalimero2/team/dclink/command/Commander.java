package com.kalimero2.team.dclink.command;

import net.kyori.adventure.audience.Audience;

public interface Commander extends Audience {
    boolean hasPermission(String permission);
}
