package utils;

import org.slf4j.LoggerFactory;

public final class Logger {

    private Logger() {
    }

    public static void debug(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).debug(message);
    }

    public static void info(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).info(message);
    }

    public static void warn(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).warn(message);
    }

    public static void error(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).error(message);
    }

    public static void error(Class<?> clazz, String message, Throwable throwable) {
        LoggerFactory.getLogger(clazz).error(message, throwable);
    }
}
