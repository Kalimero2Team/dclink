package com.kalimero2.team.dclink.velocity.command;

import com.kalimero2.team.dclink.command.BrigadierSetup;
import com.kalimero2.team.dclink.command.PlatformHandler;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.velocity.VelocityDCLink;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;

import java.util.ArrayList;
import java.util.List;

public class VelocityCommandHandler implements PlatformHandler {
    private final VelocityDCLink dcLink;

    public VelocityCommandHandler(final VelocityDCLink dcLink) {
        this.dcLink = dcLink;
    }

    @Override
    public CommandManager<Sender> createCommandManager() {
        try {
            final VelocityCommandManager<Sender> commandManager = new VelocityCommandManager<>(
                    dcLink.getServer().getPluginManager().getPlugin("dclink-velocity").orElse(null),
                    this.dcLink.getServer(),
                    ExecutionCoordinator.simpleCoordinator(),
                    SenderMapper.create(VelocitySender::from, sender -> ((VelocitySender) sender).source())
            );
            BrigadierSetup.setup(commandManager);
            return commandManager;
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to initialize command manager", ex);
        }
    }

    @Override
    public <C> List<String> playerArgumentSuggestions(CommandContext<C> commandContext) {
        List<String> output = new ArrayList<>();
        dcLink.getServer().getAllPlayers().forEach(player -> output.add(player.getUsername()));
        return output;
    }

}
