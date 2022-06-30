package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.DCLinkCodes;
import com.kalimero2.team.dclink.DCLinkConfig;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DiscordAccountLinker extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger("dclink-discord");
    private final DCLink dcLink;
    private final JDA jda;
    private final DCLinkConfig.DiscordConfiguration discordConfiguration;
    private boolean giveRoleWhenLinked = false;

    protected DiscordAccountLinker(DCLink dcLink, JDA jda) {
        this.dcLink = dcLink;
        this.jda = jda;
        this.discordConfiguration = dcLink.getConfig().discordConfiguration;

        Guild guild = jda.getGuildById(discordConfiguration.getGuild());
        if (guild == null) {
            logger.error("Could not find guild with id {}", discordConfiguration.getGuild());
            return;
        }
        String linkRoleId = discordConfiguration.getLinkRole();
        if(linkRoleId == null){
            logger.info("No link role configured");
        }else{
            Role linkRole = guild.getRoleById(linkRoleId);
            if(linkRole == null){
                logger.error("Could not find role with id {}", linkRoleId);
                giveRoleWhenLinked = false;
            }else{
                giveRoleWhenLinked = true;
            }
        }
        jda.addEventListener(this);
        sendLinkChannelMessage();
    }

    private void sendLinkChannelMessage(){
        TextChannel linkChannel = jda.getTextChannelById(discordConfiguration.getLinkChannel());
        if(linkChannel == null){
            logger.error("Could not find link channel with id {}", discordConfiguration.getLinkChannel());
            return;
        }
        boolean found = linkChannel.retrievePinnedMessages().complete().stream().anyMatch(message -> message.getAuthor().getId().equals(jda.getSelfUser().getId()));
        if(!found){
            Message message = linkChannel.sendMessage("TODO: Link channel message").setActionRows(
                    ActionRow.of(Button.primary("add", "TODO: Add Button Message").asEnabled())
            ).complete();
            message.pin().queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        boolean isSelf = event.getAuthor().getId().equals(jda.getSelfUser().getId());
        // Delete Pin Message
        if(isSelf && event.getChannel().getId().equals(discordConfiguration.getLinkChannel()) && event.getMessage().getType().equals(MessageType.CHANNEL_PINNED_ADD)){
            event.getMessage().delete().queue();
        }
        if(isSelf || event.getAuthor().isBot()){
            return;
        }

        if(event.getChannelType().equals(ChannelType.TEXT)){
            TextChannel channel = event.getTextChannel();
            Category parentCategory = channel.getParentCategory();
            if(parentCategory != null && parentCategory.getId().equals(discordConfiguration.getLinkCategory())){
                if(channel.getTopic() != null && channel.getTopic().equals(event.getAuthor().getId())){
                    String code = event.getMessage().getContentStripped();
                    MinecraftPlayer minecraftPlayer = DCLinkCodes.getPlayer(code);
                    if(minecraftPlayer != null){
                        DiscordAccount discordAccount = dcLink.getDiscordAccount(event.getAuthor().getId());
                        Collection<MinecraftPlayer> linkedPlayers = discordAccount.getLinkedPlayers();

                        int java = 0;
                        int bedrock = 0;

                        for(MinecraftPlayer linkedPlayer:linkedPlayers){
                            if(dcLink.isBedrock(linkedPlayer)){
                                bedrock++;
                            }else{
                                java++;
                            }
                        }

                        boolean overBedrockLimit = bedrock > dcLink.getConfig().linkingConfiguration.getBedrockLimit();
                        boolean overJavaLimit = java > dcLink.getConfig().linkingConfiguration.getJavaLimit();

                        if(overBedrockLimit && dcLink.isBedrock(minecraftPlayer)) {
                            event.getMessage().reply("TODO: Bedrock limit reached").queue();
                            return;
                        } else if (overJavaLimit) {
                            event.getMessage().reply("TODO: Java limit reached").queue();
                            return;
                        }

                        event.getMessage().reply("TODO: Rules Message").setActionRows(
                                ActionRow.of(
                                        Button.success("accept","TODO: Accept Button Message"),
                                        Button.danger("decline","TODO: Decline Button Message")
                                )
                        ).queue();

                    }else{
                        event.getMessage().reply("TODO: Invalid code message").queue();
                    }
                }
            }

        }

    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        // TODO: Unlink Player when they leave the Guild
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Guild guild = jda.getGuildById(discordConfiguration.getGuild());
        if (guild == null) {
            logger.error("Could not find guild with id {}", discordConfiguration.getGuild());
            return;
        }

        if(event.getComponentId().equals("add")){
            TextChannel textChannel = null;
            Optional<TextChannel> optionalTextChannel = guild.getTextChannelsByName("link", false).stream().filter(channel -> Objects.equals(channel.getTopic(), event.getUser().getId())).findFirst();
            if(optionalTextChannel.isPresent()){
                textChannel = optionalTextChannel.get();
            }

            if(textChannel == null){
                Category category = guild.getCategoryById(discordConfiguration.getLinkCategory());
                if(category != null){
                    EnumSet<Permission> allow = EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND);
                    textChannel = category.createTextChannel("link").setTopic(event.getUser().getId()).addMemberPermissionOverride(event.getUser().getIdLong(), allow,null).complete();
                    textChannel.sendMessage(event.getUser().getAsMention()).queue();
                    textChannel.sendMessage("TODO: CHANNEL MESSAGE").
                            setActionRows(ActionRow.of(Button.primary("cancel", "TODO: CANCEL MESSAGE"))).queue();

                    textChannel.delete().queueAfter(10, TimeUnit.MINUTES);
                }
            }

            if(textChannel != null){
                event.reply(textChannel.getAsMention()).setEphemeral(true).queue();
            }

        }else if(event.getChannel().getName().equals("link")){
            if(event.getComponentId().equals("cancel")){
                event.getChannel().delete().queue();
                return;
            }

            if(event.getComponentId().equals("accept")){
                // TODO Link Player
                logger.info(event.getUser().getName() + " accepted the rules");
                event.reply("message.discord.rules_accepted").setEphemeral(true).queue();
            } else if (event.getComponentId().equals("deny")){
                logger.info(event.getUser().getName() + " denied the rules");
                event.reply("message.discord.rules_denied").setEphemeral(true).queue();
            }
            event.getChannel().delete().queueAfter(3, TimeUnit.SECONDS);
        }
    }

}
