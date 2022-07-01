package com.kalimero2.team.dclink.paper.commands;

import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.DCLinkCodes;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.paper.PaperDCLink;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkCommand extends CommandHandler{
    protected LinkCommand(PaperDCLink dcLink, CommandManager commandManager) {
        super(dcLink, commandManager);
    }

    @Override
    public void register() {
        if(dcLink.isLoaded()){
            if (dcLink.getConfig().linkingConfiguration != null && !dcLink.getConfig().linkingConfiguration.linkRequired) {
                commandManager.command(commandManager.commandBuilder("link")
                        .permission("dclink.command.link")
                .handler(this::linkSelf));
            }
        }
    }

    private void linkSelf(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player){
            if (dcLink.getMessages().minecraftMessages != null) {
                MinecraftPlayer minecraftPlayer = dcLink.getMinecraftPlayer(player.getUniqueId());
                Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.linkCodeMessage, Placeholder.unparsed("code", DCLinkCodes.addPlayer(minecraftPlayer)));
                player.sendMessage(message);
            }
        }
    }

}
