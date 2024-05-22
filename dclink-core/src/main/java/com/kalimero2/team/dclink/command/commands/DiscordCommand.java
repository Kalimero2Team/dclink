package com.kalimero2.team.dclink.command.commands;

import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.PlayerSender;
import com.kalimero2.team.dclink.command.argument.MinecraftPlayerComponent;
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
                .optional(MinecraftPlayerComponent.of("player"))
                .permission("dclink.command.discord")
                .handler(this::info));
    }

    private void info(CommandContext<Sender> context) {
        DCLinkMessages messages = dcLink.getMessages();
        Optional<MinecraftPlayer> optionalMinecraftPlayer = context.optional("player");
        MinecraftPlayer minecraftPlayer;
        if (context.sender() instanceof PlayerSender commander) {
            minecraftPlayer = optionalMinecraftPlayer.orElse(commander.player());
        } else {
            minecraftPlayer = optionalMinecraftPlayer.orElse(null);
            if (minecraftPlayer == null) {
                Component message = messages.getMinifiedMessage(messages.getMinecraftMessages().needsArgumentIfExecutedByConsole);
                context.sender().sendMessage(message);
                return;
            }
        }

        DiscordAccount discordAccount = minecraftPlayer.getDiscordAccount();
        if (discordAccount == null) {
            Component message = messages.getMinifiedMessage(messages.getMinecraftMessages().notLinked);
            context.sender().sendMessage(message);
        } else {
            String name = discordAccount.getName() + "#" + discordAccount.getDiscriminator();
            Component message = messages.getMinifiedMessage(messages.getMinecraftMessages().discordCommand, Placeholder.unparsed("discord_id", discordAccount.getId()), Placeholder.unparsed("discord_name", name));
            context.sender().sendMessage(message);
        }
    }
}
