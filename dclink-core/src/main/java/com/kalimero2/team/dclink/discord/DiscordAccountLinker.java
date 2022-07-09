package com.kalimero2.team.dclink.discord;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.DCLinkCodes;
import com.kalimero2.team.dclink.DCLinkConfig;
import com.kalimero2.team.dclink.DCLinkMessages;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiscordAccountLinker extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger("dclink-discord");
    private final DCLink dcLink;
    private final JDA jda;
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

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        boolean isSelf = event.getAuthor().getId().equals(jda.getSelfUser().getId());
        // Deletes Pin Message
        if(isSelf && event.getChannel().getId().equals(config.linkChannel) && event.getMessage().getType().equals(MessageType.CHANNEL_PINNED_ADD)){
            event.getMessage().delete().queue();
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

        if(event.getChannel().getId().equals(config.linkChannel)){
            DiscordAccount discordAccount = dcLink.getDiscordAccount(event.getUser().getId());
            if (event.getComponentId().equals("add")){
                TextInput code = TextInput.create("code", messages.modalInputDescription, TextInputStyle.SHORT)
                        .setRequiredRange(4,4)
                        .setRequired(true).
                        build();
                Modal modal = Modal.create("linkModal", messages.modalTitle).addActionRows(ActionRow.of(code)).build();
                event.replyModal(modal).queue();
            }else if (event.getComponentId().equals("accept")){
                if(preLinkedPlayers.containsKey(discordAccount)){
                    MinecraftPlayer minecraftPlayer = preLinkedPlayers.get(discordAccount);
                    dcLink.linkAccounts(minecraftPlayer, discordAccount);
                    preLinkedPlayers.remove(discordAccount);
                    logger.info(event.getUser().getName() + " accepted the rules");
                    event.editMessage(messages.rulesAccepted).setActionRows().queue();
                    if(giveRoleWhenLinked){
                        guild.addRoleToMember(event.getUser(), Objects.requireNonNull(guild.getRoleById(config.linkRole))).queue();
                    }
                }else {
                    logger.error(event.getUser().getName() + " tried to accept the rules but was not prelinked");
                    event.editMessage(messages.genericLinkError).setActionRows().queue();
                }
            } else if (event.getComponentId().equals("decline")){
                if(preLinkedPlayers.containsKey(discordAccount)){
                    preLinkedPlayers.remove(discordAccount);
                    logger.info(event.getUser().getName() + " declined the rules");
                    event.editMessage(messages.rulesDenied).setActionRows().queue();
                }else {
                    logger.error(event.getUser().getName() + " tried to decline the rules but was not prelinked");
                    event.editMessage(messages.genericLinkError).setActionRows().queue();
                }
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event){
        if(event.getModalId().equals("linkModal")){
            String code = Objects.requireNonNull(event.getValue("code")).getAsString();
            logger.info("{} entered {}", event.getUser().getName(), code);
            MinecraftPlayer minecraftPlayer = DCLinkCodes.getPlayer(code);
            if(minecraftPlayer != null){
                DiscordAccount discordAccount = dcLink.getDiscordAccount(event.getUser().getId());
                Collection<MinecraftPlayer> linkedPlayers = discordAccount.getLinkedPlayers();

                if(linkedPlayers.contains(minecraftPlayer)){
                    event.reply(messages.alreadyLinked).setEphemeral(true).queue();
                    preLinkedPlayers.remove(discordAccount);
                    return;
                }

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

                boolean isBedrock = dcLink.isBedrock(minecraftPlayer);
                boolean isJava = !isBedrock;
                logger.info("{} is attempting to link {} which is a {} Account", event.getUser().getName(), minecraftPlayer.getName(), isBedrock ? "Bedrock":"Java");

                if(overBedrockLimit && isBedrock) {
                    logger.info("Link for {} failed because has linked {} bedrock accounts, which is over the limit of {}", event.getUser().getName(), bedrock, dcLink.getConfig().linkingConfiguration.bedrockLimit);
                    event.reply(messages.maxBedrock).setEphemeral(true).queue();
                    return;
                } else if (overJavaLimit && isJava) {
                    logger.info("Link for {} failed because has linked {} bedrock accounts, which is over the limit of {}", event.getUser().getName(), java, dcLink.getConfig().linkingConfiguration.javaLimit);
                    event.reply(messages.maxJava).setEphemeral(true).queue();
                    return;
                }

                preLinkedPlayers.put(discordAccount, minecraftPlayer);

                event.reply(messages.rules).setEphemeral(true).addActionRows(ActionRow.of(
                                Button.success("accept",messages.accept),
                                Button.danger("decline",messages.decline)
                        )
                ).queue();
            }else{
                event.reply(messages.wrongCode).setEphemeral(true).queue();
            }
        }
    }

}
