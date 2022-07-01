package com.kalimero2.team.dclink.paper.commands;

import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.paper.PaperDCLink;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AltsCommand extends CommandHandler{
    protected AltsCommand(PaperDCLink dcLink, CommandManager commandManager) {
        super(dcLink, commandManager);
    }

    @Override
    public void register() {
        if(dcLink.isLoaded()){
            commandManager.command(commandManager.commandBuilder("alts")
                    .argument(OfflinePlayerArgument.of("player"))
                    .permission("dclink.command.alts")
            .handler(this::alts));
        }
    }

    private void alts(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player){
            OfflinePlayer target = context.getOrDefault("player",player);
            if(target != null){
                DiscordAccount discordAccount = dcLink.getMinecraftPlayer(target.getUniqueId()).getDiscordAccount();
                if(discordAccount == null){
                    Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.notLinked);
                    player.sendMessage(message);
                }else{
                    StringBuilder alts = new StringBuilder();
                    discordAccount.getLinkedPlayers().forEach(linkedPlayer -> alts.append(linkedPlayer.getName()).append(" "));
                    if (dcLink.getMessages().minecraftMessages != null) {
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.altsCommand, Placeholder.unparsed("alts", alts.toString()));
                        player.sendMessage(message);
                    }
                }
            }
        }
    }
}
