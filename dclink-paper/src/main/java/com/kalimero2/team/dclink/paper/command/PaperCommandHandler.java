package com.kalimero2.team.dclink.paper.command;

import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.PlatformHandler;
import com.kalimero2.team.dclink.paper.PaperDCLink;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;
import java.util.List;

public class PaperCommandHandler implements PlatformHandler {
    private final PaperDCLink dcLink;

    public PaperCommandHandler(final PaperDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Sender> createCommandManager() {
        final PaperCommandManager<Sender> commandManager;
        try {
            commandManager = new PaperCommandManager<>(
                    this.dcLink.getPlugin(),
                    ExecutionCoordinator.simpleCoordinator(),
                    SenderMapper.create(PaperSender::from, sender -> ((PaperSender) sender).sender())
            );
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to initialize command manager", ex);
        }

        commandManager.registerBrigadier();
        BrigadierSetup.setup(commandManager);

        return commandManager;
    }

    @Override
    public <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext) {
        List<String> output = new ArrayList<>();
        dcLink.getPlugin().getServer().getOnlinePlayers().forEach(player -> output.add(player.getName()));
        return output;
    }

}
