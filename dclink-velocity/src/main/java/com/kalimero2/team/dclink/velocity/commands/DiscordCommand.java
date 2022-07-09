package com.kalimero2.team.dclink.velocity.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.velocity.arguments.PlayerArgument;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.velocity.VelocityDCLink;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class DiscordCommand extends CommandHandler{
    protected DiscordCommand(VelocityDCLink dcLink, CommandManager commandManager) {
        super(dcLink, commandManager);
    }

    @Override
    public void register() {
        if(dcLink.isLoaded()){
            commandManager.command(commandManager.commandBuilder("discord","dc")
                    .argument(PlayerArgument.optional("player"))
                    .permission("dclink.command.discord")
                    .handler(this::info));
        }
    }

    private void info(CommandContext<CommandSource> context) {
        if(context.getSender() instanceof Player player){
            Player target = context.getOrDefault("player",player);
            if(target != null){
                DiscordAccount discordAccount = dcLink.getMinecraftPlayer(target.getUniqueId()).getDiscordAccount();
                if (dcLink.getMessages().minecraftMessages != null) {
                    if(discordAccount == null){
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.notLinked);
                        player.sendMessage(message);
                    }else{
                        String name = discordAccount.getName() + "#" + discordAccount.getDiscriminator();
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.discordCommand, Placeholder.unparsed("discord_id", discordAccount.getId()), Placeholder.unparsed("discord_name",name));
                        player.sendMessage(message);
                    }
                }
            }
        }
    }
}
