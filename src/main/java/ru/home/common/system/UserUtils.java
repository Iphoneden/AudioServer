package ru.home.common.system;

import org.slf4j.Logger;
import ru.home.common.logger.Slf4Logger;

import java.net.InetAddress;

class UserUtils {

    private static Logger logger = Slf4Logger.getLogger();

    static String getIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.toString();
        } catch (Exception ex) {
            logger.error("Failed to obtain ip");
        }
        return null;
    }

    static String getComputerName() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            return host.getHostName();
        } catch (Exception ex) {
            logger.error("Failed to obtain host name");
        }
        return null;
    }

    static String getUserName() {
        return System.getProperty("user.name");
    }
}
