package com.kalimero2.team.dclink.hytale.command;

import com.kalimero2.team.dclink.command.PlatformHandler;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.hytale.HytaleDCLink;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import java.util.ArrayList;
import java.util.List;

public class HytaleCommandHandler implements PlatformHandler {
    private final HytaleDCLink dcLink;

    public HytaleCommandHandler(HytaleDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Sender> createCommandManager() {
        throw new UnsupportedOperationException("Hytale command registration not yet implemented");
    }

    @Override
    public <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext) {
        return new ArrayList<>();
    }
}
