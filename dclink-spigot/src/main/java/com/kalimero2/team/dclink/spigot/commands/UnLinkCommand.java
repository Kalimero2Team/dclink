package com.kalimero2.team.dclink.spigot.commands;

import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.spigot.SpigotDCLink;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnLinkCommand extends CommandHandler{
    protected UnLinkCommand(SpigotDCLink dcLink, CommandManager commandManager) {
        super(dcLink, commandManager);
    }

    @Override
    public void register() {
        if(dcLink.isLoaded()){
            commandManager.command(commandManager.commandBuilder("unlink")
                    .argument(OfflinePlayerArgument.of("player"))
                    .permission("dclink.command.unlink")
            .handler(this::unLink));
        }
    }

    private void unLink(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player){
            OfflinePlayer target = context.getOrDefault("player",player);
            if(target != null){
                MinecraftPlayer minecraftPlayer = dcLink.getMinecraftPlayer(target.getUniqueId());
                DiscordAccount discordAccount = minecraftPlayer.getDiscordAccount();
                if (dcLink.getMessages().minecraftMessages != null) {
                    if(discordAccount == null){
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.notLinked);
                        player.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
                    }else{
                        dcLink.unLinkAccount(minecraftPlayer);
                        Component message = dcLink.getMessages().getMinifiedMessage(dcLink.getMessages().minecraftMessages.unLinkCommand, Placeholder.unparsed("player", minecraftPlayer.getName()));
                        player.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
                    }
                }
            }
        }
    }
}
