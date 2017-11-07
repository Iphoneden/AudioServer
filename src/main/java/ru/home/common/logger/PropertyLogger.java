package ru.home.common.logger;

import org.apache.log4j.Level;

public class PropertyLogger {

    private static final String LOG_FILE_NAME = "log.log";

    public enum TYPE {CONSOLE, FILE, NONE, ALL}

    public enum LEVEL {
        OFF(Level.OFF), FATAL(Level.FATAL), ERROR(Level.ERROR), WARN(Level.WARN), INFO(Level.INFO), DEBUG(Level.DEBUG), TRACE(Level.TRACE), ALL(Level.ALL);

        private Level level;

        public Level getLevel() {
            return level;
        }

        LEVEL(Level level) {
            this.level = level;
        }
    }

    private LEVEL logLevel = LEVEL.ALL;
    private TYPE logType = TYPE.CONSOLE;
    private String logFile = LOG_FILE_NAME;

    public LEVEL getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LEVEL logLevel) {
        this.logLevel = logLevel;
    }

    public TYPE getLogType() {
        return logType;
    }

    public void setLogType(TYPE logType) {
        this.logType = logType;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    @Override
    public String toString() {
        return "PropertyLogger{" +
                "logLevel=" + logLevel +
                ", logType=" + logType +
                ", logFile='" + logFile + '\'' +
                '}';
    }
}
