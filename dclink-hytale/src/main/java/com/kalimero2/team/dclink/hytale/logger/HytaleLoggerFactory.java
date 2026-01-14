package com.kalimero2.team.dclink.hytale.logger;

import com.hypixel.hytale.logger.HytaleLogger;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class HytaleLoggerFactory implements ILoggerFactory {

    @Override
    public Logger getLogger(String name) {
        return new HytaleLoggerWrapper(HytaleLogger.get(name));
    }
}
