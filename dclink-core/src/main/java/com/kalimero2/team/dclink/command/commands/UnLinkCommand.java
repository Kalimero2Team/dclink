package com.kalimero2.team.dclink.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.argument.MinecraftPlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class UnLinkCommand extends DCLinkCommand {

    public UnLinkCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        CommandManager<Commander> commandManager = commands.commandManager();
        commandManager.command(commandManager.commandBuilder("unlink")
                .argument(MinecraftPlayerArgument.of("player"))
                .permission("dclink.command.unlink")
                .handler(this::unLink));
    }

    private void unLink(CommandContext<Commander> context) {
        MinecraftPlayer minecraftPlayer = context.get("player");
        DiscordAccount discordAccount = minecraftPlayer.getDiscordAccount();

        if(discordAccount == null){
            Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().getMinecraftMessages().notLinked);
            context.getSender().sendMessage(message);
        }else{
            dcLink.unLinkAccount(minecraftPlayer);
            Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().getMinecraftMessages().unLinkCommand, Placeholder.unparsed("player", minecraftPlayer.getName()));
            context.getSender().sendMessage(message);
        }
    }
}
