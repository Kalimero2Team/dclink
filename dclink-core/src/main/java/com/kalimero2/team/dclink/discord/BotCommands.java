package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BotCommands extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger("dclink-discord");
    private final DCLink dcLink;
    private final JDA jda;

    protected BotCommands(DCLink dcLink, JDA jda, String guildId) {
        this.dcLink = dcLink;
        this.jda = jda;
        registerCommands(guildId);
        jda.addEventListener(this);
    }


    private void registerCommands(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            logger.error("Could not find guild with id {}", guildId);
            return;
        }
        Optional<Command> lookupCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("lookup")).findAny();

        if (lookupCommand.isEmpty()) {
            guild.upsertCommand("lookup", "Get information about a player").addSubcommands(
                            new SubcommandData("minecraft", "Via Minecraft Username")
                                    .addOption(OptionType.STRING, "minecraft", "Minecraft Username", true),
                            new SubcommandData("discord", "Via Discord User")
                                    .addOption(OptionType.USER, "discorduser", "Discord User", true))
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                    .queue();
        }

        Optional<Command> optionalLinkCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("link")).findAny();
        if (optionalLinkCommand.isPresent()) {
            Command linkCommand = optionalLinkCommand.get();
            if (linkCommand.getOptions().stream().noneMatch(option -> option.getName().equals("discorduser"))) {
                linkCommand.editCommand()
                        .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                        .addOption(OptionType.STRING, "minecraft", "Minecraft Account", true)
                        .queue();
            }
        } else {
            guild.upsertCommand("link", "Manually link a Discord Account to a Minecraft Account")
                    .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                    .addOption(OptionType.STRING, "minecraft", "Minecraft Account", true)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                    .queue();
        }

        Optional<Command> optionalUnLinkCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("unlink")).findAny();
        if (optionalUnLinkCommand.isPresent()) {
            Command unLinkCommand = optionalUnLinkCommand.get();
            if (unLinkCommand.getOptions().stream().noneMatch(option -> option.getName().equals("discorduser"))) {
                unLinkCommand.editCommand()
                        .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                        .addOption(OptionType.STRING, "minecraft", "Minecraft Account", false)
                        .queue();
            }
        } else {
            guild.upsertCommand("unlink", "Manually removes link between a Discord Account and one/more Minecraft Accounts")
                    .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                    .addOption(OptionType.STRING, "minecraft", "Minecraft Account", false)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                    .queue();
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("lookup")) {
            lookupCommand(event);
        } else if (event.getName().equals("link")) {
            linkCommand(event);
        } else if (event.getName().equals("unlink")) {
            unLinkCommand(event);
        } else {
            String id = "rmCmd" + event.getCommandId();
            event.reply("I can't handle that command right now :(").setEphemeral(true).addComponents(ActionRow.of(Button.danger(id, "Delete Command"))).queue();
            logger.error("Unhandled command {}", event.getName());
        }
    }

    private void lookupCommand(SlashCommandInteractionEvent event) {
        OptionMapping discordOption = event.getOption("discorduser");
        OptionMapping minecraftOption = event.getOption("minecraft");
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            return;
        }

        if (subcommandName.equals("discord") && discordOption != null) {
            User user = discordOption.getAsUser();
            Collection<MinecraftPlayer> linkedPlayers = dcLink.getDiscordAccount(user.getId()).getLinkedPlayers();
            if (!linkedPlayers.isEmpty()) {
                StringBuilder message = new StringBuilder("Minecraft accounts linked to " + user.getAsMention() + ": ");
                linkedPlayers.forEach(minecraftPlayer -> message.append(minecraftPlayer.getName()).append(" "));
                event.reply(message.toString()).setEphemeral(true).queue();
            } else {
                event.reply("No Minecraft accounts linked to " + user.getAsMention()).setEphemeral(true).queue();
            }
        } else if (subcommandName.equals("minecraft") && minecraftOption != null) {
            String name = minecraftOption.getAsString();
            UUID uuid = dcLink.getUUID(name);
            if (uuid == null) {
                event.reply("Could not find a minecraft account named " + name).setEphemeral(true).queue();
                return;
            }
            MinecraftPlayer minecraftPlayer = dcLink.getMinecraftPlayer(uuid);
            if (minecraftPlayer != null && minecraftPlayer.getDiscordAccount() != null) {
                User discordUser = jda.retrieveUserById(minecraftPlayer.getDiscordAccount().getId()).complete();
                event.reply("Discord account linked to " + name + ": " + discordUser.getAsMention()).setEphemeral(true).queue();
            } else {
                event.reply("No Discord accounts linked to " + name).setEphemeral(true).queue();
            }
        }
    }

    private void linkCommand(SlashCommandInteractionEvent event) {
        OptionMapping discorduser = event.getOption("discorduser");
        OptionMapping minecraft = event.getOption("minecraft");
        if (discorduser != null && minecraft != null) {
            User user = discorduser.getAsUser();
            DiscordAccount discordAccount = dcLink.getDiscordAccount(user.getId());

            UUID minecraftUuid;
            String playerName = minecraft.getAsString();
            try {
                minecraftUuid = UUID.fromString(playerName);
            } catch (IllegalArgumentException ignored) {
                minecraftUuid = dcLink.getUUID(playerName);
            }

            if (minecraftUuid == null) {
                event.reply("Could not find Minecraft account with name " + playerName).setEphemeral(true).queue();
            } else {
                MinecraftPlayer minecraftPlayer = dcLink.getMinecraftPlayer(minecraftUuid);
                if (minecraftPlayer == null) {
                    try {
                        dcLink.getStorage().createMinecraftPlayer(minecraftUuid, playerName);
                        minecraftPlayer = dcLink.getMinecraftPlayer(minecraftUuid);
                    } catch (SQLException e) {
                        logger.error("Couldn't create MinecraftPlayer Object for (UUID " + minecraftUuid + ")");
                    }
                }

                if (discordAccount.getLinkedPlayers().contains(minecraftPlayer)) {
                    event.reply("Minecraft account " + minecraftPlayer.getName() + " is already linked to " + user.getAsMention()).setEphemeral(true).queue();
                } else {
                    if (dcLink.linkAccounts(minecraftPlayer, discordAccount)) {
                        event.reply("Linked " + user.getAsMention() + " to " + minecraftPlayer.getName()).setEphemeral(true).queue();
                    } else {
                        event.reply("Could not link " + user.getAsMention() + " to " + minecraftPlayer.getName()).setEphemeral(true).queue();
                    }
                }
            }
        }
    }

    private void unLinkCommand(SlashCommandInteractionEvent event) {
        OptionMapping discorduser = event.getOption("discorduser");
        OptionMapping minecraft = event.getOption("minecraft");

        if (discorduser != null) {
            DiscordAccount discordAccount = dcLink.getDiscordAccount(discorduser.getAsUser().getId());
            List<MinecraftPlayer> removeLinkList = new ArrayList<>();
            if (minecraft == null) {
                removeLinkList.addAll(discordAccount.getLinkedPlayers());
            } else {
                MinecraftPlayer minecraftPlayer = dcLink.getMinecraftPlayer(dcLink.getUUID(minecraft.getAsString()));
                removeLinkList.add(minecraftPlayer);
            }
            StringBuilder message = new StringBuilder("Unlinked " + discorduser.getAsUser().getAsMention() + " from ");
            removeLinkList.forEach(minecraftPlayer -> message.append(minecraftPlayer.getName()).append(" "));
            removeLinkList.forEach(dcLink::unLinkAccount);
            event.reply(message.toString()).setEphemeral(true).queue();
        }
    }

}
