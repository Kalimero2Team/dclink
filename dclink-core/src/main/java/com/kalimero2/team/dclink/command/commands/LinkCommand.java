package com.kalimero2.team.dclink.command.commands;

import com.kalimero2.team.dclink.DCLinkCodes;
import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.PlayerSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class LinkCommand extends DCLinkCommand {
    public LinkCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        if (!dcLink.getConfig().getLinkingConfiguration().isLinkRequired()) {
            CommandManager<Sender> commandManager = commands.commandManager();
            commandManager.command(commandManager.commandBuilder("link")
                    .permission("dclink.command.link")
                    .handler(this::linkSelf));
        }
    }

    private void linkSelf(CommandContext<Sender> context) {
        if (context.sender() instanceof PlayerSender player) {
            GamePlayer gamePlayer = player.player();
            DCLinkMessages messages = commands.getDCLink().getMessages();
            Component message = messages.getMinifiedMessage(messages.getGameMessages().linkCodeMessage, Placeholder.unparsed("code", DCLinkCodes.addPlayer(gamePlayer)));
            player.sendMessage(message);
        }
    }

}
