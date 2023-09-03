package com.kalimero2.team.dclink.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Sender;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.argument.MinecraftPlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class AltsCommand extends DCLinkCommand {
    public AltsCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        CommandManager<Sender> commandManager = commands.commandManager();
        commandManager.command(commandManager.commandBuilder("alts")
                .argument(MinecraftPlayerArgument.of("player"))
                .permission("dclink.command.alts")
                .handler(this::alts));
    }

    private void alts(CommandContext<Sender> context) {
        DCLinkMessages messages = dcLink.getMessages();
        DCLinkMessages.MinecraftMessages minecraftMessages = messages.getMinecraftMessages();

        MinecraftPlayer minecraftPlayer = context.get("player");
        DiscordAccount discordAccount = minecraftPlayer.getDiscordAccount();

        if (discordAccount == null) {
            Component message = messages.getMinifiedMessage(minecraftMessages.notLinked);
            context.getSender().sendMessage(message);
        } else {
            StringBuilder alts = new StringBuilder();
            discordAccount.getLinkedPlayers().forEach(linkedPlayer -> alts.append(linkedPlayer.getName()).append(" "));
            Component message = messages.getMinifiedMessage(minecraftMessages.altsCommand, Placeholder.unparsed("alts", alts.toString()));
            context.getSender().sendMessage(message);
        }
    }
}
