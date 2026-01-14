package com.kalimero2.team.dclink.fabric.command;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.minecraft.GamePlayer;
import com.kalimero2.team.dclink.command.PlayerSender;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.fabric.mixin.CommandSourceStackAccess;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FabricSender implements Sender, ForwardingAudience.Single {
    private final CommandSourceStack stack;

    private FabricSender(final CommandSourceStack stack) {
        this.stack = stack;
    }

    public static Sender from(final CommandSourceStack stack) {
        if (((CommandSourceStackAccess) stack).source() instanceof ServerPlayer) {
            return new Player(stack);
        }
        return new FabricSender(stack);
    }

    @Override
    public @NotNull Audience audience() {
        return this.stack;
    }

    public CommandSourceStack stack() {
        return this.stack;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final FabricSender that = (FabricSender) o;
        return ((CommandSourceStackAccess) this.stack).source().equals(((CommandSourceStackAccess) that.stack).source());
    }

    @Override
    public int hashCode() {
        return Objects.hash(((CommandSourceStackAccess) this.stack).source());
    }

    public static final class Player extends FabricSender implements PlayerSender {
        private Player(final CommandSourceStack stack) {
            super(stack);
        }

        @Override
        public GamePlayer player() {
            try {
                return DCLinkApi.getApi().getGamePlayer(this.stack().getPlayerOrException().getUUID());
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final FabricSender.Player that = (FabricSender.Player) o;
            return this.player().equals(that.player());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.player());
        }
    }
}
