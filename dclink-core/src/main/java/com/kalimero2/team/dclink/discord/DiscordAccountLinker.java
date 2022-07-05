package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.DCLinkCodes;
import com.kalimero2.team.dclink.DCLinkConfig;
import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DiscordAccountLinker extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger("dclink-discord");
    private final DCLink dcLink;
    private final JDA jda;
    private final String linkChannelName = "link";
    private final DCLinkConfig.DiscordConfiguration config;
    private final DCLinkMessages.DiscordBotMessages messages;
    private final Map<DiscordAccount, MinecraftPlayer> preLinkedPlayers;
    private boolean giveRoleWhenLinked = false;

    protected DiscordAccountLinker(DCLink dcLink, JDA jda) {
        this.dcLink = dcLink;
        this.jda = jda;
        this.config = dcLink.getConfig().discordConfiguration;
        this.messages = dcLink.getMessages().discordBotMessages;
        this.preLinkedPlayers = new HashMap<>();

        Guild guild = jda.getGuildById(config.guild);
        if (guild == null) {
            logger.error("Could not find guild with id {}", config.guild);
            return;
        }
        String linkRoleId = config.linkRole;
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
        deleteOldLinkChannels();
    }

    private void sendLinkChannelMessage(){
        TextChannel linkChannel = jda.getTextChannelById(config.linkChannel);
        if(linkChannel == null){
            logger.error("Could not find link channel with id {}", config.linkChannel);
            return;
        }
        boolean found = linkChannel.retrievePinnedMessages().complete().stream().anyMatch(message -> message.getAuthor().getId().equals(jda.getSelfUser().getId()));
        if(!found){
            Message message = linkChannel.sendMessage(messages.infoChannel).setActionRows(
                    ActionRow.of(Button.primary("add", messages.add).asEnabled())
            ).complete();
            message.pin().queue();
        }
    }

    private void deleteOldLinkChannels(){
        if(config.autoDeleteLinkChannelsOnRestart){
            jda.getGuilds().forEach(guild -> guild.getTextChannels().stream().filter(channel -> channel.getName().equals(linkChannelName)).forEach(channel -> channel.delete().queue()));
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        boolean isSelf = event.getAuthor().getId().equals(jda.getSelfUser().getId());
        // Deletes Pin Message
        if(isSelf && event.getChannel().getId().equals(config.linkChannel) && event.getMessage().getType().equals(MessageType.CHANNEL_PINNED_ADD)){
            event.getMessage().delete().queue();
        }
        if(isSelf || event.getAuthor().isBot()){
            return;
        }

        if(event.getChannelType().equals(ChannelType.TEXT)){
            TextChannel channel = event.getTextChannel();
            Category parentCategory = channel.getParentCategory();
            if(parentCategory != null && parentCategory.getId().equals(config.linkCategory)){
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

                        boolean overBedrockLimit = bedrock >= dcLink.getConfig().linkingConfiguration.bedrockLimit;
                        boolean overJavaLimit = java >= dcLink.getConfig().linkingConfiguration.javaLimit;

                        if(overBedrockLimit && dcLink.isBedrock(minecraftPlayer)) {
                            event.getMessage().reply(messages.maxBedrock).queue();
                            return;
                        } else if (overJavaLimit) {
                            event.getMessage().reply(messages.maxJava).queue();
                            return;
                        }

                        preLinkedPlayers.put(discordAccount, minecraftPlayer);

                        event.getMessage().reply(messages.rules).setActionRows(
                                ActionRow.of(
                                        Button.success("accept",messages.accept),
                                        Button.danger("decline",messages.decline)
                                )
                        ).queue();

                    }else{
                        event.getMessage().reply(messages.wrongCode).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        dcLink.unLinkAccounts(dcLink.getDiscordAccount(event.getUser().getId()));
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Guild guild = jda.getGuildById(config.guild);
        if (guild == null) {
            logger.error("Could not find guild with id {}", config.guild);
            return;
        }

        if(event.getComponentId().equals("add")){
            TextChannel textChannel = null;
            Optional<TextChannel> optionalTextChannel = guild.getTextChannelsByName(linkChannelName, false).stream().filter(channel -> Objects.equals(channel.getTopic(), event.getUser().getId())).findFirst();
            if(optionalTextChannel.isPresent()){
                textChannel = optionalTextChannel.get();
            }
            if(textChannel == null){
                Category category = guild.getCategoryById(config.linkCategory);
                if(category != null){
                    EnumSet<Permission> allow = EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND);
                    textChannel = category.createTextChannel(linkChannelName).setTopic(event.getUser().getId()).addMemberPermissionOverride(event.getUser().getIdLong(), allow,null).complete();
                    textChannel.sendMessage(event.getUser().getAsMention()).queue();
                    textChannel.sendMessage(messages.trustChannel).
                            setActionRows(ActionRow.of(Button.primary("cancel", messages.cancel))).queue();

                    textChannel.delete().queueAfter(10, TimeUnit.MINUTES);
                }
            }

            if(textChannel != null){
                event.reply(textChannel.getAsMention()).setEphemeral(true).queue();
            }

        }else if(event.getChannel().getName().equals(linkChannelName)){
            if(event.getComponentId().equals("cancel")){
                event.getChannel().delete().queue();
                return;
            }
            DiscordAccount discordAccount = dcLink.getDiscordAccount(event.getUser().getId());
            if(event.getComponentId().equals("accept")){
                MinecraftPlayer minecraftPlayer = preLinkedPlayers.get(discordAccount);
                dcLink.linkAccounts(minecraftPlayer, discordAccount);
                logger.info(event.getUser().getName() + " accepted the rules");
                event.reply(messages.rulesAccepted).setEphemeral(true).queue();
            } else if (event.getComponentId().equals("decline")){
                preLinkedPlayers.remove(discordAccount);
                logger.info(event.getUser().getName() + " declined the rules");
                event.reply(messages.rulesDenied).setEphemeral(true).queue();
            }
            event.getChannel().delete().queueAfter(3, TimeUnit.SECONDS);
        }
    }

}
