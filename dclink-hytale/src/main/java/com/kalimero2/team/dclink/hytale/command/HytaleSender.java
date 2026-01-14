package com.kalimero2.team.dclink.hytale.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import com.kalimero2.team.dclink.command.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class HytaleSender implements Sender {

    private final IMessageReceiver sender;

    private HytaleSender(IMessageReceiver sender) {
        this.sender = sender;
    }

    public static HytaleSender from(Object sender) {
        if (sender instanceof IMessageReceiver receiver) {
            return new HytaleSender(receiver);
        }
        throw new IllegalArgumentException("Sender must be an IMessageReceiver");
    }

    public Object getSender() {
        return sender;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        String plain = PlainTextComponentSerializer.plainText().serialize(message);
        sender.sendMessage(Message.raw(plain));
    }
}
