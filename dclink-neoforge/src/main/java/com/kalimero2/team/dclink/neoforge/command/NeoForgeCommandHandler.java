package com.kalimero2.team.dclink.neoforge.command;

import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.PlatformHandler;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.neoforge.NeoForgeDCLink;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.neoforge.NeoForgeServerCommandManager;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeCommandHandler implements PlatformHandler {
    private final NeoForgeDCLink dcLink;

    public NeoForgeCommandHandler(final NeoForgeDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Sender> createCommandManager() {
        final NeoForgeServerCommandManager<Sender> commandManager = new NeoForgeServerCommandManager<>(
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.create(NeoForgeSender::from,
                        sender -> ((NeoForgeSender) sender).stack())
        );

        BrigadierSetup.setup(commandManager);


        return commandManager;
    }

    @Override
    public <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext) {
        List<String> output = new ArrayList<>();
        dcLink.getServer().getPlayerList().getPlayers().forEach(player -> output.add(player.getName().getString()));
        return output;
    }
}
