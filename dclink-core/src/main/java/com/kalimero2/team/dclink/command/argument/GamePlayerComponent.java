package com.kalimero2.team.dclink.command.argument;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.minecraft.GamePlayer;
import com.kalimero2.team.dclink.command.Commands;
import com.kalimero2.team.dclink.command.Sender;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.component.TypedCommandComponent;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.UUID;

public class GamePlayerComponent<C>{

    public static TypedCommandComponent.Builder<Sender, GamePlayer> of(final String name) {

        BlockingSuggestionProvider.Strings<Sender> suggestions = (context, input) -> {
            return context.get(Commands.PLATFORMCOMMANDS).playerArgumentSuggestions(context);
        };

        return CommandComponent.<Sender, GamePlayer>ofType(GamePlayer.class, name)
                .suggestionProvider(suggestions)
                .parser(stringParser().flatMapSuccess(GamePlayer.class,(commandContext, commandInput) -> {
                    DCLink dcLink = commandContext.get(Commands.DCLINK);

                    UUID uuid = dcLink.getUUID(commandInput);

                    if (uuid == null) {
                        return ArgumentParseResult.failureFuture(new IllegalArgumentException("No such player found"));
                    }
                    GamePlayer player = dcLink.getGamePlayer(uuid);
                    if (player == null) {
                        return ArgumentParseResult.failureFuture(new IllegalArgumentException("No Such Player"));
                    }

                    return ArgumentParseResult.successFuture(player);
                }));
    }


}
