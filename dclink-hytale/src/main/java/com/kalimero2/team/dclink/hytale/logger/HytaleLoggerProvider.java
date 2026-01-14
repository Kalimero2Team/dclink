package com.kalimero2.team.dclink.hytale.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class HytaleLoggerProvider implements SLF4JServiceProvider {

    private final String REQUESTED_API_VERSION = "2.0.0";

    private HytaleLoggerFactory hytaleLoggerFactory;

    @Override
    public ILoggerFactory getLoggerFactory() {
        return hytaleLoggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return new BasicMarkerFactory();
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return new BasicMDCAdapter();
    }

    @Override
    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    @Override
    public void initialize() {
        hytaleLoggerFactory = new HytaleLoggerFactory();
    }
}
