package com.kalimero2.team.dclink.command.commands;

import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.PlayerSender;
import com.kalimero2.team.dclink.command.argument.GamePlayerComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import java.util.Optional;

public class DiscordCommand extends DCLinkCommand {
    public DiscordCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        CommandManager<Sender> commandManager = commands.commandManager();
        commandManager.command(commandManager.commandBuilder("discord", "dc")
                .optional(GamePlayerComponent.of("player"))
                .permission("dclink.command.discord")
                .handler(this::info));
    }

    private void info(CommandContext<Sender> context) {
        DCLinkMessages messages = dcLink.getMessages();
        Optional<GamePlayer> optionalGamePlayer = context.optional("player");
        GamePlayer gamePlayer;
        if (context.sender() instanceof PlayerSender commander) {
            gamePlayer = optionalGamePlayer.orElse(commander.player());
        } else {
            gamePlayer = optionalGamePlayer.orElse(null);
            if (gamePlayer == null) {
                Component message = messages.getMinifiedMessage(messages.getGameMessages().needsArgumentIfExecutedByConsole);
                context.sender().sendMessage(message);
                return;
            }
        }

        DiscordAccount discordAccount = gamePlayer.getDiscordAccount();
        if (discordAccount == null) {
            Component message = messages.getMinifiedMessage(messages.getGameMessages().notLinked);
            context.sender().sendMessage(message);
        } else {
            String name = discordAccount.getName() + "#" + discordAccount.getDiscriminator();
            Component message = messages.getMinifiedMessage(messages.getGameMessages().discordCommand, Placeholder.unparsed("discord_id", discordAccount.getId()), Placeholder.unparsed("discord_name", name));
            context.sender().sendMessage(message);
        }
    }
}
