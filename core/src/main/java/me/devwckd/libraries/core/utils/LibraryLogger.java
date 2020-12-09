package me.devwckd.libraries.core.utils;

import lombok.RequiredArgsConstructor;

import java.util.logging.Logger;

import static java.lang.String.*;

@RequiredArgsConstructor
public class LibraryLogger {

    private final Logger logger;
    private final String pluginName;

    public void severe(String message, Object... objects) {
        logger.severe(format("[" + pluginName + "] " + message, objects));
    }

    public void warning(String message, Object... objects) {
        logger.warning(format("[" + pluginName + "] " + message, objects));
    }

    public void info(String message, Object... objects) {
        logger.info(format("[" + pluginName + "] " + message, objects));
    }

    public void config(String message, Object... objects) {
        logger.config(format("[" + pluginName + "] " + message, objects));
    }

    public void fine(String message, Object... objects) {
        logger.fine(format("[" + pluginName + "] " + message, objects));
    }

    public void finer(String message, Object... objects) {
        logger.finer(format("[" + pluginName + "] " + message, objects));
    }

    public void finest(String message, Object... objects) {
        logger.finest(format("[" + pluginName + "] " + message, objects));
    }

}
