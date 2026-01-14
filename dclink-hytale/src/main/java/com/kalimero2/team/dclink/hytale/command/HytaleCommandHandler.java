package com.kalimero2.team.dclink.hytale.command;

import com.kalimero2.team.dclink.command.PlatformHandler;
import com.kalimero2.team.dclink.command.Sender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.ExecutionCoordinator;

import java.util.ArrayList;
import java.util.List;

public class HytaleCommandHandler implements PlatformHandler {

    @Override
    public CommandManager<Sender> createCommandManager() {
        return new HytaleCommandManager<>(
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.create(HytaleSender::from, s -> ((HytaleSender) s).sender())
        );
    }

    @Override
    public <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext) {
        return new ArrayList<>();
    }
}
