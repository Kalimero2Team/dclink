package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.DCLinkConfig;
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

import java.util.Collection;
import java.util.Optional;

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
        Optional<Command> optionalCommand = guild.retrieveCommands().complete().stream().filter(command -> command.getName().equals("minecraft")).findAny();
        if(optionalCommand.isPresent()){
            Command command = optionalCommand.get();
            if(command.getOptions().stream().noneMatch(option -> option.getName().equals("discorduser"))){
                command.editCommand().addOption(OptionType.USER, "discorduser", "User", true).queue();
            }
        }else{
            guild.upsertCommand("minecraft", "Minecraft User Lookup").addOption(OptionType.USER, "discorduser","User",true,true).setDefaultPermissions(DefaultMemberPermissions.DISABLED).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if (event.getName().equals("minecraft")){
            OptionMapping discorduser = event.getOption("discorduser");
            if(discorduser != null){
                User user = discorduser.getAsUser();
                Collection<MinecraftPlayer> linkedPlayers = dcLink.getDiscordAccount(user.getId()).getLinkedPlayers();
                if(linkedPlayers.size() > 0){
                    StringBuilder message = new StringBuilder("Minecraft accounts linked to " + user.getName() + ": ");
                    linkedPlayers.forEach(minecraftPlayer -> message.append(minecraftPlayer.getName()).append(" "));
                    event.reply(message.toString()).setEphemeral(true).queue();
                }else{
                    event.reply("No Minecraft accounts linked to " + user.getName()).setEphemeral(true).queue();
                }
            }
        }else {
            event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
            logger.error("Unhandled command {}", event.getName());
        }
    }


}
