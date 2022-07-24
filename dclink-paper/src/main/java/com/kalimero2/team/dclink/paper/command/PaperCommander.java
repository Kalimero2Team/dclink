package com.kalimero2.team.dclink.paper.command;

import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.PlayerCommander;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class PaperCommander implements Commander, ForwardingAudience.Single {
    private final CommandSender sender;

    private PaperCommander(CommandSender sender){
        this.sender = sender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public Audience audience() {
        return this.sender;
    }

    public CommandSender sender() {
        return this.sender;
    }

    public static PaperCommander from(final CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player player) {
            return new Player(player);
        }
        return new PaperCommander(sender);
    }


    public static final class Player extends PaperCommander implements PlayerCommander {
        private Player(final org.bukkit.entity.Player sender) {
            super(sender);
        }

        public org.bukkit.entity.Player bukkit() {
            return (org.bukkit.entity.Player) this.sender();
        }

        @Override
        public MinecraftPlayer player() {
            return DCLinkApi.getApi().getMinecraftPlayer(bukkit().getUniqueId());
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final PaperCommander.Player that = (PaperCommander.Player) o;
            return this.sender().equals(that.sender());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.sender());
        }
    }
}
