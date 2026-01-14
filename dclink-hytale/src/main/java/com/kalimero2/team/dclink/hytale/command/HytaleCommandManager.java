package com.kalimero2.team.dclink.hytale.command;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.jetbrains.annotations.NotNull;

public class HytaleCommandManager<C> extends CommandManager<C> {

    private final SenderMapper<Object, C> senderMapper;

    public HytaleCommandManager(
            final @NonNull ExecutionCoordinator<C> executionCoordinator,
            final @NonNull SenderMapper<Object, C> senderMapper
    ) {
        super(executionCoordinator, (c) -> true);
        this.senderMapper = senderMapper;
    }

    public SenderMapper<Object, C> getSenderMapper() {
        return this.senderMapper;
    }

    @Override
    public boolean hasPermission(@NotNull C sender, @NonNull String permission) {
        IMessageReceiver reverse = (IMessageReceiver) senderMapper.reverse(sender);
        if(reverse instanceof Player player) {
            return player.hasPermission(permission);
        }
        return true;
    }

    @Override
    public @NonNull CommandRegistrationHandler<C> commandRegistrationHandler() {
        return command -> {
            command.components().forEach(component -> {
                var hytaleCommand = new HytaleCloudCommand<>(HytaleCommandManager.this, component);
                com.hypixel.hytale.server.core.command.system.CommandManager.get().register(hytaleCommand);
            });

            return true;
        };
    }
}
