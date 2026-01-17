package com.kalimero2.team.dclink.hytale.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import com.kalimero2.team.dclink.api.DCLinkApi;
import com.kalimero2.team.dclink.api.game.GamePlayer;
import com.kalimero2.team.dclink.command.PlayerSender;
import com.kalimero2.team.dclink.command.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HytaleSender implements Sender {

    private final IMessageReceiver sender;

    private HytaleSender(IMessageReceiver sender) {
        this.sender = sender;
    }

    public static HytaleSender from(Object sender) {
        if (sender instanceof Player player) {
            return new HytalePlayer(player);
        } else if (sender instanceof IMessageReceiver receiver) {
            return new HytaleSender(receiver);
        }
        throw new IllegalArgumentException("Sender must be an IMessageReceiver");
    }

    public static @NotNull Message convertComponent(@NotNull Component message) {
        Message hytaleMessage;
        if (message instanceof TextComponent textComponent) {
            hytaleMessage = Message.raw(textComponent.content());

            TextDecoration.State bold = textComponent.decoration(TextDecoration.BOLD);
            if (bold != TextDecoration.State.NOT_SET) {
                hytaleMessage.bold(bold == TextDecoration.State.TRUE);
            }

            TextDecoration.State italic = textComponent.decoration(TextDecoration.ITALIC);
            if (italic != TextDecoration.State.NOT_SET) {
                hytaleMessage.italic(italic == TextDecoration.State.TRUE);
            }

            ClickEvent clickEvent = textComponent.clickEvent();
            if (clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
                ClickEvent.Payload payload = clickEvent.payload();
                if (payload instanceof ClickEvent.Payload.Text text) {
                    hytaleMessage.link(text.value());
                }
            }

            TextColor color = textComponent.color();
            if (color != null) {
                hytaleMessage.color(color.asHexString());
            }

            textComponent.children().forEach(component -> {
                hytaleMessage.insert(convertComponent(component));
            });
        } else {
            hytaleMessage = Message.raw(PlainTextComponentSerializer.plainText().serialize(message));
        }
        return hytaleMessage;
    }

    public Object sender() {
        return sender;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        sender.sendMessage(convertComponent(message));
    }

    public static final class HytalePlayer extends HytaleSender implements PlayerSender {
        private HytalePlayer(final Player sender) {
            super(sender);
        }

        public Player hytale() {
            return (Player) this.sender();
        }

        @Override
        @SuppressWarnings("removal")
        public GamePlayer player() {
            return DCLinkApi.getApi().getGamePlayer(hytale().getUuid());
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final HytaleSender.HytalePlayer that = (HytaleSender.HytalePlayer) o;
            return this.sender().equals(that.sender());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.sender());
        }
    }
}
