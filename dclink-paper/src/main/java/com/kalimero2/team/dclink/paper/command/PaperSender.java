package com.kalimero2.team.dclink.paper.command;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.PlayerSender;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PaperSender implements Sender, ForwardingAudience.Single {
    private final CommandSourceStack commandSourceStack;

    private PaperSender(CommandSourceStack commandSourceStack) {
        this.commandSourceStack = commandSourceStack;
    }

    public static PaperSender from(final CommandSourceStack commandSourceStack) {
        if (commandSourceStack.getSender() instanceof org.bukkit.entity.Player player) {
            return new PaperPlayer(commandSourceStack, player);
        }
        return new PaperSender(commandSourceStack);
    }

    @Override
    public @NotNull Audience audience() {
        return this.commandSourceStack.getSender();
    }

    public CommandSourceStack sender() {
        return this.commandSourceStack;
    }

    public static final class PaperPlayer extends PaperSender implements PlayerSender {

        private final Player player;

        private PaperPlayer(CommandSourceStack commandSourceStack, final Player player) {
            super(commandSourceStack);
            this.player = player;
        }

        public Player bukkit() {
            return player;
        }

        @Override
        public GamePlayer player() {
            return DCLinkApi.getApi().getGamePlayer(bukkit().getUniqueId());
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final PaperPlayer that = (PaperPlayer) o;
            return this.sender().equals(that.sender());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.sender());
        }
    }
}
