package ru.home.common.logger;

import ru.dns.prop4s.ManagerProperty;
import ru.dns.prop4s.common.Property;
import ru.home.common.logger.PropertyLogger.TYPE;

import java.io.IOException;
import java.util.Enumeration;

import static org.apache.log4j.Level.OFF;

public class Slf4Logger {

    private static final int MAX_FILE_SIZE = 1000000;
    private static final int MAX_BACKUP_INDEX = 2;
    private static final String CONSOLE_APPENDER_NAME = "console";
    private static final String FILE_APPENDER_NAME = "file";

    private final static org.apache.log4j.ConsoleAppender ca = (org.apache.log4j.ConsoleAppender) org.apache.log4j.Logger.getRootLogger().getAppender(CONSOLE_APPENDER_NAME);

    private Slf4Logger() {
    }

    public static org.slf4j.Logger getLogger() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final String callingClassName = stackTrace[2].getClassName();
        return org.slf4j.LoggerFactory.getLogger(callingClassName);
    }

    public static void setup(Property<PropertyLogger> property) {
        PropertyLogger prop = property.getProperty();
        org.apache.log4j.RollingFileAppender fa = null;
        org.apache.log4j.Level level = prop.getLogLevel().getLevel();
        if (OFF.equals(level) || prop.getLogType() == TYPE.NONE) {
            org.apache.log4j.Logger.getRootLogger().removeAppender(FILE_APPENDER_NAME);
        } else if (prop.getLogType() == TYPE.CONSOLE) {
            org.apache.log4j.Logger.getRootLogger().removeAppender(FILE_APPENDER_NAME);
        } else if (prop.getLogType() == TYPE.FILE) {
            level = OFF;
            fa = getRollingFileAppender();
            org.apache.log4j.Logger.getRootLogger().addAppender(fa);
        } else if (prop.getLogType() == TYPE.ALL) {
            fa = getRollingFileAppender();
            org.apache.log4j.Logger.getRootLogger().addAppender(fa);
        }
        ca.setThreshold(level);
        ca.activateOptions();
        if (fa != null) {
            fa.setThreshold(level);
            fa.setFile(prop.getLogFile());
            fa.activateOptions();
        }
        getLogger().debug(property.toString());
    }

    public static void update() throws IOException {
        if (ManagerProperty.update(PropertyLogger.class)) {
            setup(ManagerProperty.getProperty(PropertyLogger.class));
        }
    }

    private static org.apache.log4j.RollingFileAppender getRollingFileAppender() {
        org.apache.log4j.RollingFileAppender fa = getAppender(org.apache.log4j.RollingFileAppender.class, FILE_APPENDER_NAME);
        if (fa == null) {
            fa = new org.apache.log4j.RollingFileAppender();
            fa.setLayout(ca.getLayout());
            fa.setAppend(true);
            fa.setImmediateFlush(true);
            fa.setMaximumFileSize(MAX_FILE_SIZE);
            fa.setMaxBackupIndex(MAX_BACKUP_INDEX);
        }
        return fa;
    }

    @SuppressWarnings("unchecked")
    private static <APPENDER extends org.apache.log4j.Appender> APPENDER getAppender(Class<APPENDER> appenderClass, String appenderName) {
        for (Enumeration loggers = org.apache.log4j.LogManager.getCurrentLoggers(); loggers.hasMoreElements(); ) {
            org.apache.log4j.Logger logger = (org.apache.log4j.Logger) loggers.nextElement();
            for (Enumeration appenders = logger.getParent().getAllAppenders(); appenders.hasMoreElements(); ) {
                APPENDER appender = (APPENDER) appenders.nextElement();
                if (appenderClass.equals(appender.getClass()) && appenderName.equalsIgnoreCase(appender.getName())) {
                    return appender;
                }
            }
        }
        return null;
    }

}
