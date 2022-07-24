package com.kalimero2.team.dclink.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.function.BiFunction;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.command.Commands;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

@DefaultQualifier(NonNull.class)
public final class MinecraftPlayerArgument<C> extends CommandArgument<C, MinecraftPlayer> {

    private MinecraftPlayerArgument(
            final boolean required,
            final String name,
            final String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final ArgumentDescription defaultDescription
    ) {
        super(required, name, new Parser<>(), defaultValue, MinecraftPlayer.class, suggestionsProvider, defaultDescription);
    }

    public static <C> Builder<C> builder(final String name) {
        return new Builder<>(name);
    }

    public static <C> MinecraftPlayerArgument<C> of(final String name) {
        return MinecraftPlayerArgument.<C>builder(name).build();
    }

    public static <C> MinecraftPlayerArgument<C> optional(final String name) {
        return MinecraftPlayerArgument.<C>builder(name).asOptional().build();
    }

    public static <C> MinecraftPlayerArgument<C> optional(final String name, final String defaultValue) {
        return MinecraftPlayerArgument.<C>builder(name).asOptionalWithDefault(defaultValue).build();
    }

    public static final class Builder<C> extends CommandArgument.TypedBuilder<C, MinecraftPlayer, Builder<C>> {
        private Builder(final String name) {
            super(MinecraftPlayer.class, name);
        }

        @Override
        public MinecraftPlayerArgument<C> build() {
            return new MinecraftPlayerArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }

    public static final class Parser<C> implements ArgumentParser<C, MinecraftPlayer> {
        @Override
        public ArgumentParseResult<MinecraftPlayer> parse(final CommandContext<C> commandContext, final Queue<String> inputQueue) {
            final @Nullable String input = inputQueue.peek();
            if (input == null) {
                return failure(new NoInputProvidedException(Parser.class, commandContext));
            }

            DCLink dcLink = commandContext.get(Commands.DCLINK);
            UUID uuid = dcLink.getUUID(input);
            final @Nullable MinecraftPlayer player = dcLink.getMinecraftPlayer(uuid);
            if (player == null) {
                return failure(new IllegalArgumentException("No Such Player"));
            }

            inputQueue.remove();
            return success(player);
        }

        @Override
        public List<String> suggestions(final CommandContext<C> commandContext, final String input) {
            return List.of("");
        }
    }

}
