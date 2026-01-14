package com.kalimero2.team.dclink.command;

import com.kalimero2.team.dclink.api.game.GamePlayer;

public interface PlayerSender extends Sender {

    GamePlayer player();

}
