package com.kalimero2.team.dclink.command.commands;

import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.argument.GamePlayerComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class UnLinkCommand extends DCLinkCommand {

    public UnLinkCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        CommandManager<Sender> commandManager = commands.commandManager();
        commandManager.command(commandManager.commandBuilder("unlink")
                .required(GamePlayerComponent.of("player"))
                .permission("dclink.command.unlink")
                .handler(this::unLink));
    }

    private void unLink(CommandContext<Sender> context) {
        GamePlayer gamePlayer = context.get("player");
        DiscordAccount discordAccount = gamePlayer.getDiscordAccount();

        if (discordAccount == null) {
            Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().getGameMessages().notLinked);
            context.sender().sendMessage(message);
        } else {
            dcLink.unLinkAccount(gamePlayer);
            Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().getGameMessages().unLinkCommand, Placeholder.unparsed("player", gamePlayer.getName()));
            context.sender().sendMessage(message);
        }
    }
}
