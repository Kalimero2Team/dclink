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
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
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
        this.config = dcLink.getConfig().getDiscordConfiguration();
        this.messages = dcLink.getMessages().getDiscordBotMessages();
        this.preLinkedPlayers = new HashMap<>();

        Guild guild = jda.getGuildById(config.getGuild());
        if (guild == null) {
            logger.error("Could not find guild with id {}", config.getGuild());
            return;
        }
        String linkRoleId = config.getLinkRole();
        if (linkRoleId == null || linkRoleId.equals("")) {
            logger.info("No link role configured");
        } else {
            Role linkRole = guild.getRoleById(linkRoleId);
            if (linkRole == null) {
                logger.error("Could not find role with id {}", linkRoleId);
                giveRoleWhenLinked = false;
            } else {
                giveRoleWhenLinked = true;
            }
        }
        jda.addEventListener(this);
        sendLinkChannelMessage();
    }

    private void sendLinkChannelMessage() {
        GuildMessageChannel linkChannel = jda.getTextChannelById(config.getLinkChannel());
        if (linkChannel == null) {
            linkChannel = jda.getNewsChannelById(config.getLinkChannel());
        }
        if (linkChannel == null) {
            linkChannel = jda.getThreadChannelById(config.getLinkChannel());
        }
        if (linkChannel == null) {
            logger.error("Could not find link channel with id {}", config.getLinkChannel());
            return;
        }
        boolean found = linkChannel.retrievePinnedMessages().complete().stream().anyMatch(message -> message.getAuthor().getId().equals(jda.getSelfUser().getId()));
        if (!found) {
            Message message = linkChannel.sendMessage(messages.infoChannel).setComponents(
                    ActionRow.of(Button.primary("add", messages.add).asEnabled())
            ).complete();
            message.pin().queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean isSelf = event.getAuthor().getId().equals(jda.getSelfUser().getId());
        // Deletes Pin Message
        if (isSelf && event.getChannel().getId().equals(config.getLinkChannel()) && event.getMessage().getType().equals(MessageType.CHANNEL_PINNED_ADD)) {
            event.getMessage().delete().queue();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        dcLink.unLinkAccounts(dcLink.getDiscordAccount(event.getUser().getId()));
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        if (componentId.startsWith("rmCmd")) {
            String commandId = componentId.replace("rmCmd", "");
            if (event.getGuild() != null && event.getGuild().retrieveCommandById(commandId).complete() != null) {
                event.getGuild().deleteCommandById(commandId).complete();
            } else {
                jda.deleteCommandById(commandId).complete();
            }

            event.editMessage("Removed Command").setComponents().queue();
        }

        Guild guild = jda.getGuildById(config.getGuild());
        if (guild == null) {
            logger.error("Could not find guild with id {}", config.getGuild());
            return;
        }

        if (event.getChannel().getId().equals(config.getLinkChannel())) {
            DiscordAccount discordAccount = dcLink.getDiscordAccount(event.getUser().getId());
            switch (componentId) {
                case "add" -> {
                    TextInput code = TextInput.create("code", messages.modalInputDescription, TextInputStyle.SHORT)
                            .setRequiredRange(4, 4)
                            .setRequired(true).
                            build();
                    Modal modal = Modal.create("linkModal", messages.modalTitle).addComponents(ActionRow.of(code)).build();
                    event.replyModal(modal).queue();
                }
                case "accept" -> {
                    if (preLinkedPlayers.containsKey(discordAccount)) {
                        MinecraftPlayer minecraftPlayer = preLinkedPlayers.get(discordAccount);
                        dcLink.linkAccounts(minecraftPlayer, discordAccount);
                        preLinkedPlayers.remove(discordAccount);
                        logger.info(event.getUser().getAsTag() + " accepted the rules");
                        event.editMessage(messages.rulesAccepted).setComponents().queue();
                        if (giveRoleWhenLinked) {
                            discordAccount.addRole(dcLink.getDiscordRole(config.getLinkRole()));
                        }
                    } else {
                        logger.error(event.getUser().getAsTag() + " tried to accept the rules but was not pre-linked");
                        event.editMessage(messages.genericLinkError).setComponents().queue();
                    }
                }
                case "decline" -> {
                    if (preLinkedPlayers.containsKey(discordAccount)) {
                        preLinkedPlayers.remove(discordAccount);
                        logger.info(event.getUser().getAsTag() + " declined the rules");
                        event.editMessage(messages.rulesDenied).setComponents().queue();
                    } else {
                        logger.error(event.getUser().getAsTag() + " tried to decline the rules but was not pre-linked");
                        event.editMessage(messages.genericLinkError).setComponents().queue();
                    }
                }
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("linkModal")) {
            String code = Objects.requireNonNull(event.getValue("code")).getAsString();
            logger.info("{} entered {}", event.getUser().getAsTag(), code);
            MinecraftPlayer minecraftPlayer = DCLinkCodes.getPlayer(code);
            if (minecraftPlayer != null) {
                DiscordAccount discordAccount = dcLink.getDiscordAccount(event.getUser().getId());
                Collection<MinecraftPlayer> linkedPlayers = discordAccount.getLinkedPlayers();

                if (linkedPlayers.contains(minecraftPlayer)) {
                    event.reply(messages.alreadyLinked).setEphemeral(true).queue();
                    preLinkedPlayers.remove(discordAccount);
                    return;
                }

                int java = 0;
                int bedrock = 0;

                for (MinecraftPlayer linkedPlayer : linkedPlayers) {
                    if (dcLink.isBedrock(linkedPlayer)) {
                        bedrock++;
                    } else {
                        java++;
                    }
                }

                int bedrockLimit = dcLink.getConfig().getLinkingConfiguration().getBedrockLimit();
                int javaLimit = dcLink.getConfig().getLinkingConfiguration().getJavaLimit();

                boolean overBedrockLimit = bedrock >= bedrockLimit;
                boolean overJavaLimit = java >= javaLimit;

                boolean isBedrock = dcLink.isBedrock(minecraftPlayer);
                boolean isJava = !isBedrock;

                logger.info("{} is attempting to link {} which is a {} Account", event.getUser().getAsTag(), minecraftPlayer.getName(), isBedrock ? "Bedrock" : "Java");

                if (overBedrockLimit && isBedrock) {
                    logger.info("Link for {} failed because has linked {} Bedrock accounts, which is over the limit of {}", event.getUser().getAsTag(), bedrock, bedrockLimit);
                    event.reply(messages.maxBedrock).setEphemeral(true).queue();
                    return;
                }
                if (overJavaLimit && isJava) {
                    logger.info("Link for {} failed because has linked {} Java accounts, which is over the limit of {}", event.getUser().getAsTag(), java, javaLimit);
                    event.reply(messages.maxJava).setEphemeral(true).queue();
                    return;
                }

                preLinkedPlayers.put(discordAccount, minecraftPlayer);
                DCLinkCodes.removePlayer(code);

                event.reply(messages.rules).setEphemeral(true).addComponents(ActionRow.of(
                                Button.success("accept", messages.accept),
                                Button.danger("decline", messages.decline)
                        )
                ).queue();
            } else {
                event.reply(messages.wrongCode).setEphemeral(true).queue();
            }
        }
    }

}
