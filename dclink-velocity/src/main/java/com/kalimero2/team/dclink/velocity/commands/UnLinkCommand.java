package com.kalimero2.team.dclink.velocity.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.velocity.arguments.PlayerArgument;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.velocity.VelocityDCLink;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class UnLinkCommand extends CommandHandler{
    protected UnLinkCommand(VelocityDCLink dcLink, CommandManager commandManager) {
        super(dcLink, commandManager);
    }

    @Override
    public void register() {
        if(dcLink.isLoaded()){
            commandManager.command(commandManager.commandBuilder("unlink")
                    .argument(PlayerArgument.of("player"))
                    .permission("dclink.command.unlink")
            .handler(this::unLink));
        }
    }

    private void unLink(CommandContext<CommandSource> context) {
        if(context.getSender() instanceof Player player){
            Player target = context.getOrDefault("player",player);
            if(target != null){
                MinecraftPlayer minecraftPlayer = dcLink.getMinecraftPlayer(target.getUniqueId());
                DiscordAccount discordAccount = minecraftPlayer.getDiscordAccount();
                if (dcLink.getMessages().minecraftMessages != null) {
                    if(discordAccount == null){
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.notLinked);
                        player.sendMessage(message);
                    }else{
                        dcLink.unLinkAccount(minecraftPlayer);
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.unLinkCommand, Placeholder.unparsed("player", minecraftPlayer.getName()));
                        player.sendMessage(message);
                    }
                }
            }
        }
    }
}
