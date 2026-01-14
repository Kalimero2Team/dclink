package com.kalimero2.team.dclink.velocity.command;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.minecraft.GamePlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.PlayerSender;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VelocitySender implements Sender, ForwardingAudience.Single {
    private final CommandSource source;

    private VelocitySender(CommandSource source) {
        this.source = source;
    }

    public static VelocitySender from(final CommandSource sender) {
        if (sender instanceof com.velocitypowered.api.proxy.Player player) {
            return new Player(player);
        }
        return new VelocitySender(sender);
    }

    @Override
    public @NotNull Audience audience() {
        return this.source;
    }

    public CommandSource source() {
        return this.source;
    }

    public static final class Player extends VelocitySender implements PlayerSender {
        private Player(final com.velocitypowered.api.proxy.Player player) {
            super(player);
        }

        public com.velocitypowered.api.proxy.Player velocity() {
            return (com.velocitypowered.api.proxy.Player) this.source();
        }

        @Override
        public GamePlayer player() {
            return DCLinkApi.getApi().getGamePlayer(velocity().getUniqueId());
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Player that = (Player) o;
            return this.source().equals(that.source());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.source());
        }
    }
}
