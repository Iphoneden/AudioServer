package ru.home.context;

import org.slf4j.Logger;
import ru.dns.prop4s.ManagerProperty;
import ru.dns.prop4s.common.Property;
import ru.dns.prop4s.loader.FileLoader;
import ru.home.common.StringUtils;
import ru.home.common.logger.Slf4Logger;
import ru.home.common.system.UserInfo;
import ru.home.socket.PropertySocket;
import ru.home.sound.microphone.PropertyMicrophone;
import ru.home.sound.microphone.Microphone;
import ru.home.sound.microphone.MicrophoneFactory;
import ru.home.sound.stream.MicrophoneCaptureStream;
import ru.home.sound.stream.MicrophoneStream;

import javax.sound.sampled.LineUnavailableException;

import java.io.IOException;

import static ru.home.context.ProjectConstants.SLEEP_MILLIS_INT;

public class ApplicationContext {

    private static Logger logger = Slf4Logger.getLogger();

    private Microphone microphone;
    private MicrophoneStream microphoneStream;
    private MicrophoneCaptureStream microphoneCaptureStream;

    public ApplicationContext(FileLoader loader) throws IOException {
        super();

        Property<PropertyMicrophone> property = ManagerProperty.putProperty(PropertyMicrophone.class, loader);
        logger.debug(property.toString());

        Property<PropertySocket> propertySocket = ManagerProperty.putProperty(PropertySocket.class, loader);
        logger.debug(propertySocket.toString());

        ManagerProperty.writeAll();
        //throw new RuntimeException("stop");
    }

    public void onStart() {
        String left = StringUtils.repeat(" ", 20);
        logger.debug("\n{}***************************************\n{}" +
                        "*                                     **\n{}" +
                        "*         ApplicationContext          ***\n{}" +
                        "*                                     ***\n{}" +
                        "*****************************************\n{}" +
                        "  **************************************\n"
                , left, left, left, left, left, left);
        try {
            startThreads();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        doWork();
    }

    private void startThreads() throws LineUnavailableException {
        logger.debug("Инициализация обработчиков");
        PropertyMicrophone property = ManagerProperty.property(PropertyMicrophone.class);
        if (property.isMicEnabled()) {
            microphone = MicrophoneFactory.newMicrophone();
            microphoneStream = new MicrophoneStream(microphone, MicrophoneFactory.getBeep());
            microphoneCaptureStream = new MicrophoneCaptureStream(microphoneStream);
        }
    }

    public synchronized void onEmergencySaving() {
        try {
            if (microphoneStream != null) {
                microphoneStream.saveToFile();
            }
        } catch (Exception e) {
            logger.error("Error emergency saving", e);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void doWork() {
        while (true) {
            try {
                logger.info("Живой " + getMicState());
                Thread.sleep(SLEEP_MILLIS_INT);
                Slf4Logger.update();
                update();
            } catch (Exception e) {
                logger.error("Ошибка", e);
            }
        }
    }

    private void update() throws IOException, LineUnavailableException {
        if (ManagerProperty.isNeedUpdated(PropertyMicrophone.class) && ManagerProperty.update(PropertyMicrophone.class)) {
            restartThreads();
        } else {
            PropertyMicrophone property = ManagerProperty.property(PropertyMicrophone.class);
            Microphone.setMicrophoneVolume(property.getMicVolumeNormal(), true);
        }
    }

    private void restartThreads() throws LineUnavailableException {
        logger.debug("Рестарт обработчиков");
        microphoneCaptureStream.exit();
        UserInfo.updateSystemInfo();
        PropertyMicrophone property = ManagerProperty.property(PropertyMicrophone.class);
        if (property.isMicEnabled()) {
            microphone = MicrophoneFactory.newMicrophone();
            microphoneStream = new MicrophoneStream(microphone, MicrophoneFactory.getBeep());
            microphoneCaptureStream = new MicrophoneCaptureStream(microphoneStream);
        }
    }

    private boolean isThreadMicStopped() {
        return microphone == null || microphoneStream == null || microphoneCaptureStream == null || !microphoneStream.isRunThread() || !microphoneCaptureStream.isRunThread();
    }

    private String getMicState() {
        return !isThreadMicStopped() ? "[Микрофон: " + microphoneStream.getRecordState().name() + "]" : "";
    }
}
