package ru.home.sound.stream;

import org.slf4j.Logger;
import ru.dns.prop4s.ManagerProperty;
import ru.home.common.logger.Slf4Logger;
import ru.home.sound.microphone.PropertyMicrophone;
import ru.home.sound.microphone.Microphone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class MicrophoneCaptureStream implements Runnable {

    private static Logger logger = Slf4Logger.getLogger();

    private final MicrophoneStream microphoneStream;
    private final Microphone microphone;
    private final byte[] data;
    private Thread thread;

    public MicrophoneCaptureStream(MicrophoneStream microphoneStream) {
        super();
        this.microphoneStream = microphoneStream;
        microphone = microphoneStream.getMicrophone();
        PropertyMicrophone propertyMicrophone = ManagerProperty.property(PropertyMicrophone.class);
        // открыть буффер нужной длины
        final int bufferSize = microphone.getLine().getBufferSize();
        final float sec = ((propertyMicrophone.getMicSaveAfterLengthSec() < propertyMicrophone.getMicSaveBeforeLengthSec() ? propertyMicrophone.getMicSaveAfterLengthSec() : propertyMicrophone.getMicSaveBeforeLengthSec()));
        int bufferSizeByTime = (int) (bufferSize * sec) / 32 * 32;
        bufferSizeByTime = (bufferSizeByTime < bufferSize / 100 || bufferSizeByTime > bufferSize) ? bufferSize : bufferSizeByTime;
        data = new byte[bufferSizeByTime];
        logger.debug("длина буффера обмена {}", data.length);
        thread = new Thread(this);
        start();
    }

    private void start() {
        logger.debug("@START");
        thread.start();
    }

    public synchronized void exit() {
        logger.debug("@EXIT");
        thread = null;
        microphone.getLine().close();
        microphoneStream.exit();
    }

    public boolean isRunThread() {
        return thread != null;
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            logger.debug("Ошибка", e);
        }
        thread = null;
    }

    private void doWork() throws InterruptedException, IOException {
        logger.debug("START");
        while (thread != null) {
            write();
        }
    }

    private void write() throws InterruptedException, IOException {
        Date startRec = new Date();
        int numBytesRead = microphone.getLine().read(data, 0, data.length);
        if (numBytesRead == -1) {
            throw new InterruptedException("Ошибка записи из источника " + microphone);
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byteArrayOutputStream.write(data, 0, numBytesRead);
            microphoneStream.send(startRec, new Date(), byteArrayOutputStream.toByteArray());
        }
    }

}
