package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.DCLinkConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BotCommands extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger("dclink-discord");
    private final DCLink dcLink;
    private final JDA jda;

    protected BotCommands(DCLink dcLink, JDA jda, String guildId){
        this.dcLink = dcLink;
        this.jda = jda;
        registerCommands(guildId);
        jda.addEventListener(this);
    }


    private void registerCommands(String guildId){
        Guild guild = jda.getGuildById(guildId);
        if(guild == null){
            logger.error("Could not find guild with id {}", guildId);
            return;
        }
        Optional<Command> optionalMinecraftCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("minecraft")).findAny();
        if(optionalMinecraftCommand.isPresent()){
            Command minecraftCommand = optionalMinecraftCommand.get();
            if(minecraftCommand.getOptions().stream().noneMatch(option -> option.getName().equals("discorduser"))){
                minecraftCommand.editCommand().addOption(OptionType.USER, "discorduser", "User", true).queue();
            }
        }else{
            guild.upsertCommand("minecraft", "Minecraft User Lookup")
                    .addOption(OptionType.USER, "discorduser","User",true,true)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .queue();
        }
        Optional<Command> optionalLinkCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("link")).findAny();
        if(optionalLinkCommand.isPresent()) {
            Command linkCommand = optionalLinkCommand.get();
            if (linkCommand.getOptions().stream().noneMatch(option -> option.getName().equals("discorduser"))) {
                linkCommand.editCommand()
                        .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                        .addOption(OptionType.STRING, "minecraft", "Minecraft Account", true)
                .queue();
            }
        }else{
            guild.upsertCommand("link", "Manually link a Discord Account to a Minecraft Account")
                    .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                    .addOption(OptionType.STRING, "minecraft", "Minecraft Account", true)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .queue();
        }

        Optional<Command> optionalUnLinkCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("unlink")).findAny();
        if(optionalUnLinkCommand.isPresent()) {
            Command unLinkCommand = optionalUnLinkCommand.get();
            if (unLinkCommand.getOptions().stream().noneMatch(option -> option.getName().equals("discorduser"))) {
                unLinkCommand.editCommand()
                        .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                        .addOption(OptionType.STRING, "minecraft", "Minecraft Account", false)
                        .queue();
            }
        }else{
            guild.upsertCommand("unlink", "Manually removes link between a Discord Account and one/more Minecraft Accounts")
                    .addOption(OptionType.USER, "discorduser", "Discord Account", true)
                    .addOption(OptionType.STRING, "minecraft", "Minecraft Account", false)
                    .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                    .queue();
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if (event.getName().equals("minecraft")){
            minecraftCommand(event);
        } else if (event.getName().equals("link")) {
            linkCommand(event);
        } else if (event.getName().equals("unlink")) {
            unLinkCommand(event);
        } else {
            event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
            logger.error("Unhandled command {}", event.getName());
        }
    }

    private void minecraftCommand(SlashCommandInteractionEvent event) {
        OptionMapping discorduser = event.getOption("discorduser");
        if(discorduser != null){
            User user = discorduser.getAsUser();
            Collection<MinecraftPlayer> linkedPlayers = dcLink.getDiscordAccount(user.getId()).getLinkedPlayers();
            if(linkedPlayers.size() > 0){
                StringBuilder message = new StringBuilder("Minecraft accounts linked to " + user.getAsMention() + ": ");
                linkedPlayers.forEach(minecraftPlayer -> message.append(minecraftPlayer.getName()).append(" "));
                event.reply(message.toString()).setEphemeral(true).queue();
            }else{
                event.reply("No Minecraft accounts linked to " + user.getAsMention()).setEphemeral(true).queue();
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
            try{
                minecraftUuid = UUID.fromString(minecraft.getAsString());
            }catch (IllegalArgumentException ignored){
                minecraftUuid = dcLink.getUUID(minecraft.getAsString());
            }

            if(minecraftUuid == null){
                event.reply("Could not find Minecraft account with name " + minecraft.getAsString()).setEphemeral(true).queue();
            }else{
                MinecraftPlayer minecraftPlayer = dcLink.getMinecraftPlayer(minecraftUuid);
                if(discordAccount.getLinkedPlayers().contains(minecraftPlayer)){
                    event.reply("Minecraft account " + minecraftPlayer.getName() + " is already linked to " + user.getAsMention()).setEphemeral(true).queue();
                }else{
                    dcLink.linkAccounts(minecraftPlayer, discordAccount);
                    event.reply("Linked " + user.getAsMention() + " to " + minecraftPlayer.getName()).setEphemeral(true).queue();
                }
            }
        }
    }

    private void unLinkCommand(SlashCommandInteractionEvent event) {
        OptionMapping discorduser = event.getOption("discorduser");
        OptionMapping minecraft = event.getOption("minecraft");

        if(discorduser != null){
            DiscordAccount discordAccount = dcLink.getDiscordAccount(discorduser.getAsUser().getId());
            List<MinecraftPlayer> removeLinkList = new ArrayList<>();
            if(minecraft == null) {
                removeLinkList.addAll(discordAccount.getLinkedPlayers());
            }else{
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
