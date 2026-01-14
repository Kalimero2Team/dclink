package com.kalimero2.team.dclink.paper.command;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.minecraft.GamePlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.PlayerSender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PaperSender implements Sender, ForwardingAudience.Single {
    private final CommandSender sender;

    private PaperSender(CommandSender sender) {
        this.sender = sender;
    }

    public static PaperSender from(final CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player player) {
            return new Player(player);
        }
        return new PaperSender(sender);
    }

    @Override
    public @NotNull Audience audience() {
        return this.sender;
    }

    public CommandSender sender() {
        return this.sender;
    }

    public static final class Player extends PaperSender implements PlayerSender {
        private Player(final org.bukkit.entity.Player sender) {
            super(sender);
        }

        public org.bukkit.entity.Player bukkit() {
            return (org.bukkit.entity.Player) this.sender();
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
            final PaperSender.Player that = (PaperSender.Player) o;
            return this.sender().equals(that.sender());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.sender());
        }
    }
}
