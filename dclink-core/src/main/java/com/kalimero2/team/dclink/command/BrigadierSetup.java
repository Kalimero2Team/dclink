package com.kalimero2.team.dclink.command;

import cloud.commandframework.brigadier.BrigadierManagerHolder;
import cloud.commandframework.brigadier.CloudBrigadierManager;

public final class BrigadierSetup {
    public static void setup(final BrigadierManagerHolder<Commander> manager) {
        final CloudBrigadierManager<Commander, ?> brigManager = manager.brigadierManager();
        if(brigManager != null){
            brigManager.setNativeNumberSuggestions(false);
        }
    }
}
