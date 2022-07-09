package com.kalimero2.team.dclink.spigot.commands;

import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.spigot.SpigotDCLink;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand extends CommandHandler{
    protected DiscordCommand(SpigotDCLink dcLink, CommandManager commandManager) {
        super(dcLink, commandManager);
    }

    @Override
    public void register() {
        if(dcLink.isLoaded()){
            commandManager.command(commandManager.commandBuilder("discord","dc")
                    .argument(OfflinePlayerArgument.optional("player"))
                    .permission("dclink.command.discord")
                    .handler(this::info));
        }
    }

    private void info(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player){
            OfflinePlayer target = context.getOrDefault("player",player);
            if(target != null){
                DiscordAccount discordAccount = dcLink.getMinecraftPlayer(target.getUniqueId()).getDiscordAccount();
                if (dcLink.getMessages().minecraftMessages != null) {
                    if(discordAccount == null){
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.notLinked);
                        player.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
                    }else{
                        String name = discordAccount.getName() + "#" + discordAccount.getDiscriminator();
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.discordCommand, Placeholder.unparsed("discord_id", discordAccount.getId()), Placeholder.unparsed("discord_name",name));
                        player.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
                    }
                }
            }
        }
    }
}
