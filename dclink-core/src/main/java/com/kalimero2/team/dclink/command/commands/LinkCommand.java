package com.kalimero2.team.dclink.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.DCLinkCodes;
import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commander;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.DCLinkCommand;
import com.kalimero2.team.dclink.command.PlayerCommander;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class LinkCommand extends DCLinkCommand {
    public LinkCommand(Commands commands) {
        super(commands);
    }

    @Override
    public void register() {
        if (!dcLink.getConfig().getLinkingConfiguration().isLinkRequired()) {
            CommandManager<Commander> commandManager = commands.commandManager();
            commandManager.command(commandManager.commandBuilder("link")
                    .permission("dclink.command.link")
                    .handler(this::linkSelf));
        }
    }

    private void linkSelf(CommandContext<Commander> context) {
        if(context.getSender() instanceof PlayerCommander player){
            MinecraftPlayer minecraftPlayer = player.player();
            DCLinkMessages messages = commands.getDCLink().getMessages();
            Component message = messages.getMinifiedMessage(messages.getMinecraftMessages().linkCodeMessage, Placeholder.unparsed("code", DCLinkCodes.addPlayer(minecraftPlayer)));
            player.sendMessage(message);
        }
    }

}
