package com.kalimero2.team.dclink.fabric.command;

import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.PlatformHandler;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.fabric.FabricDCLink;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.fabric.FabricServerCommandManager;

import java.util.ArrayList;
import java.util.List;

public class FabricCommandHandler implements PlatformHandler {
    private final FabricDCLink dcLink;

    public FabricCommandHandler(final FabricDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Sender> createCommandManager() {
        final FabricServerCommandManager<Sender> commandManager = new FabricServerCommandManager<>(
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.create(FabricSender::from,
                        sender -> ((FabricSender) sender).stack())
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
