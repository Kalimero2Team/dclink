package com.kalimero2.team.dclink.hytale.logger;

import com.hypixel.hytale.logger.HytaleLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

public class HytaleLoggerWrapper implements Logger {
    private final HytaleLogger logger;

    public HytaleLoggerWrapper(HytaleLogger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {}

    @Override
    public void trace(String format, Object arg) {}

    @Override
    public void trace(String format, Object arg1, Object arg2) {}

    @Override
    public void trace(String format, Object... arguments) {}

    @Override
    public void trace(String msg, Throwable t) {}

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {}

    @Override
    public void trace(Marker marker, String format, Object arg) {}

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {}

    @Override
    public void trace(Marker marker, String format, Object... argArray) {}

    @Override
    public void trace(Marker marker, String msg, Throwable t) {}

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {}

    @Override
    public void debug(String format, Object arg) {}

    @Override
    public void debug(String format, Object arg1, Object arg2) {}

    @Override
    public void debug(String format, Object... arguments) {}

    @Override
    public void debug(String msg, Throwable t) {}

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String msg) {}

    @Override
    public void debug(Marker marker, String format, Object arg) {}

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {}

    @Override
    public void debug(Marker marker, String format, Object... arguments) {}

    @Override
    public void debug(Marker marker, String msg, Throwable t) {}

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        logger.atInfo().log(msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.atInfo().log(MessageFormatter.format(format, arg).getMessage());
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.atInfo().log(MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.atInfo().log(MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.atInfo().withCause(t).log(msg);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    @Override
    public void info(Marker marker, String msg) {
        info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        info(format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        info(format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        info(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        logger.atWarning().log(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.atWarning().log(MessageFormatter.format(format, arg).getMessage());
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.atWarning().log(MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.atWarning().log(MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.atWarning().withCause(t).log(msg);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    @Override
    public void warn(Marker marker, String msg) {
        warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        warn(format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        warn(format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        warn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        logger.atSevere().log(msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.atSevere().log(MessageFormatter.format(format, arg).getMessage());
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.atSevere().log(MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.atSevere().log(MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.atSevere().withCause(t).log(msg);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

    @Override
    public void error(Marker marker, String msg) {
        error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        error(format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        error(format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        error(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        error(msg, t);
    }
}
