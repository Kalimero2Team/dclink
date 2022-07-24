package com.kalimero2.team.dclink.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.PlayerCommander;
import com.kalimero2.team.dclink.command.argument.MinecraftPlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class AltsCommand extends DCLinkCommand {
    public AltsCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        CommandManager<Commander> commandManager = commands.commandManager();
        commandManager.command(commandManager.commandBuilder("alts")
                .argument(MinecraftPlayerArgument.of("player"))
                .permission("dclink.command.alts")
                .handler(this::alts));
    }

    private void alts(CommandContext<Commander> context) {
        if(context.getSender() instanceof PlayerCommander player){
            DCLinkMessages messages = dcLink.getMessages();
            DCLinkMessages.MinecraftMessages minecraftMessages = messages.getMinecraftMessages();

            MinecraftPlayer minecraftPlayer = context.getOrDefault("player", player.player());
            DiscordAccount discordAccount = null;
            if(minecraftPlayer != null){
                discordAccount = minecraftPlayer.getDiscordAccount();
            }

            if(discordAccount == null){
                Component message = messages.getMinifiedMessage(minecraftMessages.notLinked);
                player.sendMessage(message);
            }else{
                StringBuilder alts = new StringBuilder();
                discordAccount.getLinkedPlayers().forEach(linkedPlayer -> alts.append(linkedPlayer.getName()).append(" "));
                Component message = messages.getMinifiedMessage(minecraftMessages.altsCommand, Placeholder.unparsed("alts", alts.toString()));
                player.sendMessage(message);
            }
        }
    }
}
