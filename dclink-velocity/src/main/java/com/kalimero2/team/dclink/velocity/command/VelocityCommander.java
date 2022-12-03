package com.kalimero2.team.dclink.velocity.command;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlayerCommander;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;

import java.util.Objects;

public class VelocityCommander implements Commander, ForwardingAudience.Single {
    private final CommandSource source;

    private VelocityCommander(CommandSource source) {
        this.source = source;
    }

    public static VelocityCommander from(final CommandSource sender) {
        if (sender instanceof com.velocitypowered.api.proxy.Player player) {
            return new Player(player);
        }
        return new VelocityCommander(sender);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.source.hasPermission(permission);
    }

    @Override
    public Audience audience() {
        return this.source;
    }

    public CommandSource source() {
        return this.source;
    }

    public static final class Player extends VelocityCommander implements PlayerCommander {
        private Player(final com.velocitypowered.api.proxy.Player player) {
            super(player);
        }

        public com.velocitypowered.api.proxy.Player velocity() {
            return (com.velocitypowered.api.proxy.Player) this.source();
        }

        @Override
        public MinecraftPlayer player() {
            return DCLinkApi.getApi().getMinecraftPlayer(velocity().getUniqueId());
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
