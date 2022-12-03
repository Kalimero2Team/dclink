package com.kalimero2.team.dclink.fabric.command;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlayerCommander;
import com.kalimero2.team.dclink.fabric.mixin.CommandSourceStackAccess;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class FabricCommander implements Commander, ForwardingAudience.Single {
    private final CommandSourceStack stack;

    private FabricCommander(final CommandSourceStack stack) {
        this.stack = stack;
    }

    public static FabricCommander from(final CommandSourceStack stack) {
        if (((CommandSourceStackAccess) stack).source() instanceof ServerPlayer) {
            return new Player(stack);
        }
        return new FabricCommander(stack);
    }

    @Override
    public Audience audience() {
        return this.stack;
    }

    @Override
    public boolean hasPermission(final String permission) {
        return Permissions.check(this.stack, permission, this.stack.getServer().getOperatorUserPermissionLevel());
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
        final FabricCommander that = (FabricCommander) o;
        return ((CommandSourceStackAccess) this.stack).source().equals(((CommandSourceStackAccess) that.stack).source());
    }

    @Override
    public int hashCode() {
        return Objects.hash(((CommandSourceStackAccess) this.stack).source());
    }

    public static final class Player extends FabricCommander implements PlayerCommander {
        private Player(final CommandSourceStack stack) {
            super(stack);
        }

        @Override
        public MinecraftPlayer player() {
            try {
                return DCLinkApi.getApi().getMinecraftPlayer(this.stack().getPlayerOrException().getUUID());
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
            final FabricCommander.Player that = (FabricCommander.Player) o;
            return this.player().equals(that.player());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.player());
        }
    }
}
