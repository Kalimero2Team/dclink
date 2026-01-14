package com.kalimero2.team.dclink.command;

import org.incendo.cloud.brigadier.BrigadierManagerHolder;
import org.incendo.cloud.brigadier.CloudBrigadierManager;

public final class BrigadierSetup {
    public static void setup(final BrigadierManagerHolder<Sender, ?> manager) {
        final CloudBrigadierManager<Sender, ?> brigManager = manager.brigadierManager();
        brigManager.setNativeNumberSuggestions(true);
    }
}
