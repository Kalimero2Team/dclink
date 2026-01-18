package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.api.game.GameType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
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
        String gameName = dcLink.getGameType().equals(GameType.MINECRAFT) ? "minecraft" : "hytale";

        Optional<Command> lookupCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("lookup")).findAny();

        if (lookupCommand.isEmpty()) {
            guild.upsertCommand("lookup", "Get information about a player").addSubcommands(
                            new SubcommandData(gameName, "Via ingame Username")
                                    .addOption(OptionType.STRING, gameName, "Ingame Username", true),
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
                        .addOption(OptionType.STRING, gameName, "Game Account", true)
                        .queue();
            }
        } else {
            guild.upsertCommand("link", "Manually link a Discord Account to a Game Account")
                    .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                    .addOption(OptionType.STRING, gameName, "Game Account", true)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                    .queue();
        }

        Optional<Command> optionalUnLinkCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("unlink")).findAny();
        if (optionalUnLinkCommand.isPresent()) {
            Command unLinkCommand = optionalUnLinkCommand.get();
            if (unLinkCommand.getOptions().stream().noneMatch(option -> option.getName().equals("discorduser"))) {
                unLinkCommand.editCommand()
                        .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                        .addOption(OptionType.STRING, gameName, "Game Account", false)
                        .queue();
            }
        } else {
            guild.upsertCommand("unlink", "Manually removes link between a Discord Account and one/more Game Accounts")
                    .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                    .addOption(OptionType.STRING, gameName, "Game Account", false)
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
            String message = "I can't handle that command right now :(";
            if (dcLink.getConfig().getDiscordConfiguration().isUseEmbed()) {
                event.replyEmbeds(new EmbedBuilder().setDescription(message).build()).setEphemeral(true).addComponents(ActionRow.of(Button.danger(id, "Delete Command"))).queue();
            } else {
                event.reply(message).setEphemeral(true).addComponents(ActionRow.of(Button.danger(id, "Delete Command"))).queue();
            }
            logger.error("Unhandled command {}", event.getName());
        }
    }

    private void reply(SlashCommandInteractionEvent event, String message) {
        if (dcLink.getConfig().getDiscordConfiguration().isUseEmbed()) {
            event.replyEmbeds(new EmbedBuilder().setDescription(message).build()).setEphemeral(true).queue();
        } else {
            event.reply(message).setEphemeral(true).queue();
        }
    }

    private void lookupCommand(SlashCommandInteractionEvent event) {
        OptionMapping discordOption = event.getOption("discorduser");
        String gameName = dcLink.getGameType().equals(GameType.MINECRAFT) ? "minecraft" : "hytale";
        OptionMapping gameOption = event.getOption(gameName);
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            return;
        }

        if (subcommandName.equals("discord") && discordOption != null) {
            User user = discordOption.getAsUser();
            Collection<GamePlayer> linkedPlayers = dcLink.getDiscordAccount(user.getId()).getLinkedPlayers();
            if (!linkedPlayers.isEmpty()) {
                StringBuilder message = new StringBuilder("Game accounts linked to " + user.getAsMention() + ": ");
                linkedPlayers.forEach(minecraftPlayer -> message.append(minecraftPlayer.getName()).append(" "));
                reply(event, message.toString());
            } else {
                reply(event, "No Game accounts linked to " + user.getAsMention());
            }
        } else if (subcommandName.equals(gameName) && gameOption != null) {
            String name = gameOption.getAsString();
            UUID uuid = dcLink.getUUID(name);
            if (uuid == null) {
                reply(event, "Could not find Game account with name " + name);
                return;
            }
            GamePlayer gamePlayer = dcLink.getGamePlayer(uuid);
            if (gamePlayer != null && gamePlayer.getDiscordAccount() != null) {
                User discordUser = jda.retrieveUserById(gamePlayer.getDiscordAccount().getId()).complete();
                reply(event, "Discord account linked to " + name + ": " + discordUser.getAsMention());
            } else {
                reply(event, "No Discord accounts linked to " + name);
            }
        }
    }

    private void linkCommand(SlashCommandInteractionEvent event) {
        OptionMapping discorduser = event.getOption("discorduser");
        String gameName = dcLink.getGameType().equals(GameType.MINECRAFT) ? "minecraft" : "hytale";
        OptionMapping gameOption = event.getOption(gameName);
        if (discorduser != null && gameOption != null) {
            User user = discorduser.getAsUser();
            DiscordAccount discordAccount = dcLink.getDiscordAccount(user.getId());

            UUID playerUUID;
            String playerName = gameOption.getAsString();
            try {
                playerUUID = UUID.fromString(playerName);
            } catch (IllegalArgumentException ignored) {
                playerUUID = dcLink.getUUID(playerName);
            }

            if (playerUUID == null) {
                reply(event, "Could not find Account with name " + playerName);
            } else {
                GamePlayer gamePlayer = dcLink.getGamePlayer(playerUUID);
                if (gamePlayer == null) {
                    try {
                        dcLink.getStorage().createGamePlayer(playerUUID, playerName);
                        gamePlayer = dcLink.getGamePlayer(playerUUID);
                    } catch (SQLException e) {
                        logger.error("Couldn't create GamePlayer Object for (UUID " + playerUUID + ")");
                    }
                }

                if (discordAccount.getLinkedPlayers().contains(gamePlayer)) {
                    reply(event, "Game account " + gamePlayer.getName() + " is already linked to " + user.getAsMention());
                } else {
                    if (dcLink.linkAccounts(gamePlayer, discordAccount)) {
                        reply(event, "Linked " + user.getAsMention() + " to " + gamePlayer.getName());
                    } else {
                        reply(event, "Could not link " + user.getAsMention() + " to " + gamePlayer.getName());
                    }
                }
            }
        }
    }

    private void unLinkCommand(SlashCommandInteractionEvent event) {
        OptionMapping discorduser = event.getOption("discorduser");
        String gameName = dcLink.getGameType().equals(GameType.MINECRAFT) ? "minecraft" : "hytale";
        OptionMapping gameOption = event.getOption(gameName);

        if (discorduser != null) {
            DiscordAccount discordAccount = dcLink.getDiscordAccount(discorduser.getAsUser().getId());
            List<GamePlayer> removeLinkList = new ArrayList<>();
            if (gameOption == null) {
                removeLinkList.addAll(discordAccount.getLinkedPlayers());
            } else {
                UUID uuid = dcLink.getUUID(gameOption.getAsString());
                if (uuid == null) {
                    reply(event, "Could not find Account with name " + gameOption.getAsString());
                    return;
                }
                GamePlayer gamePlayer = dcLink.getGamePlayer(uuid);
                removeLinkList.add(gamePlayer);
            }
            if(removeLinkList.isEmpty()){
                reply(event, "No Game accounts linked to " + discorduser.getAsUser().getAsMention());
                return;
            }
            StringBuilder message = new StringBuilder("Unlinked " + discorduser.getAsUser().getAsMention() + " from ");
            removeLinkList.forEach(gamePlayer -> message.append(gamePlayer.getName()).append(" "));
            removeLinkList.forEach(dcLink::unLinkAccount);
            reply(event, message.toString());
        }
    }

}
