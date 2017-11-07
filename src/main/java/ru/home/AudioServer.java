package ru.home;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import ru.dns.prop4s.ManagerProperty;
import ru.dns.prop4s.common.Property;
import ru.dns.prop4s.loader.FileLoader;
import ru.home.common.logger.Slf4Logger;
import ru.home.common.logger.PropertyLogger;
import ru.home.context.ApplicationContext;
import ru.home.context.FileLoaderDescription;

import java.io.File;
import java.io.IOException;

public class AudioServer {

    private static Logger logger = Slf4Logger.getLogger();

    private static final String DEFAULT_PROPERTY = "default.property";
    private static final String WITHOUT_PROPERTY = "without";

    private static ApplicationContext applicationContext;

    public static void main(String[] arg) throws IOException {

        String fileName = arg.length > 0 ? arg[0] : DEFAULT_PROPERTY;
        fileName = WITHOUT_PROPERTY.equalsIgnoreCase(fileName) ? null : fileName;
        File file = StringUtils.isNotEmpty(fileName) ? new File(fileName) : null;

        FileLoader loader = new FileLoaderDescription(file);
        Property<PropertyLogger> property = ManagerProperty.putProperty(PropertyLogger.class, loader);
        Slf4Logger.setup(property);

        logger.info("\r\n********************** START ***********************\r\n");
        applicationContext = new ApplicationContext(loader);

        /*
         * Add the shutdown hook
		 */
        Runtime.getRuntime().addShutdownHook(new Thread(new AudioServer.ShutdownHook()));

        applicationContext.onStart();
    }

    /**
     * A shutdown hook
     */
    private static class ShutdownHook implements Runnable {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        public void run() {
            onStop();
        }

        private void onStop() {
            logger.debug("@END");
            applicationContext.onEmergencySaving();
            logger.info("ENDS");
        }

    }

}
