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

import java.util.Optional;

public class DiscordCommand extends DCLinkCommand {
    public DiscordCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        CommandManager<Commander> commandManager = commands.commandManager();
        commandManager.command(commandManager.commandBuilder("discord","dc")
                .argument(MinecraftPlayerArgument.optional("player"))
                .permission("dclink.command.discord")
                .handler(this::info));
    }

    private void info(CommandContext<Commander> context) {
        DCLinkMessages messages = dcLink.getMessages();
        Optional<MinecraftPlayer> optionalMinecraftPlayer = context.getOptional("player");
        MinecraftPlayer minecraftPlayer;
        if(context.getSender() instanceof PlayerCommander commander){
            minecraftPlayer = optionalMinecraftPlayer.orElse(commander.player());
        }else{
            minecraftPlayer = optionalMinecraftPlayer.orElse(null);
            if(minecraftPlayer == null){
                Component message = messages.getMinifiedMessage(messages.getMinecraftMessages().needsArgumentIfExecutedByConsole);
                context.getSender().sendMessage(message);
                return;
            }
        }

        DiscordAccount discordAccount = minecraftPlayer.getDiscordAccount();
        if(discordAccount == null){
            Component message = messages.getMinifiedMessage(messages.getMinecraftMessages().notLinked);
            context.getSender().sendMessage(message);
        }else{
            String name = discordAccount.getName() + "#" + discordAccount.getDiscriminator();
            Component message = messages.getMinifiedMessage(messages.getMinecraftMessages().discordCommand, Placeholder.unparsed("discord_id", discordAccount.getId()), Placeholder.unparsed("discord_name",name));
            context.getSender().sendMessage(message);
        }
    }
}
