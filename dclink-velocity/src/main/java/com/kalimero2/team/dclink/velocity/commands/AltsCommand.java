package com.kalimero2.team.dclink.velocity.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.velocity.arguments.PlayerArgument;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.velocity.VelocityDCLink;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class AltsCommand extends CommandHandler{
    protected AltsCommand(VelocityDCLink dcLink, CommandManager commandManager) {
        super(dcLink, commandManager);
    }

    @Override
    public void register() {
        if(dcLink.isLoaded()){
            commandManager.command(commandManager.commandBuilder("alts")
                    .argument(PlayerArgument.of("player"))
                    .permission("dclink.command.alts")
            .handler(this::alts));
        }
    }

    private void alts(CommandContext<CommandSource> context) {
        if(context.getSender() instanceof Player player){
            Player target = context.getOrDefault("player",player);
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
