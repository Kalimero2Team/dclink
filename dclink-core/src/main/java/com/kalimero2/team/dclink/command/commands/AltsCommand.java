package com.kalimero2.team.dclink.command.commands;

import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.argument.GamePlayerComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class AltsCommand extends DCLinkCommand {
    public AltsCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        CommandManager<Sender> commandManager = commands.commandManager();
        commandManager.command(commandManager.commandBuilder("alts")
                .required(GamePlayerComponent.of("player"))
                .permission("dclink.command.alts")
                .handler(this::alts));
    }

    private void alts(CommandContext<Sender> context) {
        DCLinkMessages messages = dcLink.getMessages();
        DCLinkMessages.GameMessages gameMessages = messages.getGameMessages();

        GamePlayer gamePlayer = context.get("player");
        DiscordAccount discordAccount = gamePlayer.getDiscordAccount();

        if (discordAccount == null) {
            Component message = messages.getMinifiedMessage(gameMessages.notLinked);
            context.sender().sendMessage(message);
        } else {
            StringBuilder alts = new StringBuilder();
            discordAccount.getLinkedPlayers().forEach(linkedPlayer -> alts.append(linkedPlayer.getName()).append(" "));
            Component message = messages.getMinifiedMessage(gameMessages.altsCommand, Placeholder.unparsed("alts", alts.toString()));
            context.sender().sendMessage(message);
        }
    }
}
