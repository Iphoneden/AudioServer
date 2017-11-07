package ru.home.sound.stream;

import org.slf4j.Logger;
import ru.dns.prop4s.ManagerProperty;
import ru.home.common.FileUtils;
import ru.home.common.StringUtils;
import ru.home.common.logger.Slf4Logger;
import ru.home.common.system.UserInfo;
import ru.home.sound.microphone.MicrophoneFactory;
import ru.home.sound.microphone.PropertyMicrophone;
import ru.home.sound.stream.helpers.PartData;
import ru.home.sound.SoundUtils;
import ru.home.sound.microphone.Microphone;
import ru.home.sound.stream.helpers.CashFile;
import ru.home.sound.stream.helpers.CashMemory;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Math.abs;
import static ru.home.sound.stream.MicrophoneStream.State.*;

public class MicrophoneStream implements Runnable {

    private static Logger logger = Slf4Logger.getLogger();

    public enum State {
        RECORD, MICRO_PAUSE, PAUSE;

        public boolean equals(State state) {
            return this == state;
        }
    }

    private final PropertyMicrophone propertyMicrophone;
    private final Microphone microphone;
    private final Object lock = new Object();
    private final BlockingQueue<PartData> queue = new LinkedBlockingQueue<>();
    private final CashMemory cashMemory = new CashMemory();

    private Thread thread;
    private PartData partData;
    private CashFile cashFile;

    private byte[] beepMicByte;
    private State recordState = RECORD;
    private boolean writingAfterBeep;
    private boolean exit;

    public MicrophoneStream(Microphone microphone, AudioInputStream beepMic) {
        super();
        this.microphone = microphone;
        this.propertyMicrophone = ManagerProperty.property(PropertyMicrophone.class);
        try {
            this.beepMicByte = SoundUtils.getByte(beepMic);
        } catch (IOException e) {
            logger.error("Ошибка", e);
        }
        thread = new Thread(this);
        start();
    }

    public State getRecordState() {
        return recordState;
    }

    private void start() {
        logger.debug("@START");
        thread.start();
    }

    public boolean isRunThread() {
        return thread != null || exit;
    }

    Microphone getMicrophone() {
        return microphone;
    }

    void send(Date startRec, Date endDate, byte[] bytes) {
        queue.add(new PartData(startRec, endDate, bytes));
    }

    void exit() {
        logger.debug("@EXIT");
        exit = true;
        queue.add(new PartData(null, null, null));
    }

    public synchronized void saveToFile() throws IOException {
        synchronized (lock) {
            if (cashFile == null && cashMemory.isEmpty()) {
                logger.debug("@SAVE1 - Нечего писать");
                return;
            }
            cashFlushAfter(propertyMicrophone.getMicSaveAfterLengthSec());
            if (cashFile == null) {
                logger.debug("@SAVE1 - Нечего писать");
                return;
            }
            logger.debug("@SAVE1 - Запись");
            writeBeep();
            try (InputStream streamIn = cashFile.getInputStream(); AudioInputStream stream = new AudioInputStream(streamIn, microphone.getFormat(), AudioSystem.NOT_SPECIFIED)) {
                final AudioFileFormat.Type micAudioFileFormatType = propertyMicrophone.getMicAudioFileFormatType();
                final String fileName = MicrophoneFactory.genericFileName(
                        propertyMicrophone.getMicRecFileName(),
                        cashFile.getFirstRecStartDate(),
                        cashFile.getDurationMilliSec() / 1000.0,
                        UserInfo.getSystemInfo()
                );
                final File path = StringUtils.isNoEmpty(propertyMicrophone.getMicRecPath()) ? new File(propertyMicrophone.getMicRecPath()) : null;
                final File file = FileUtils.getFileName(path, fileName, micAudioFileFormatType);
                logger.debug("Сохранение в {}", file.getAbsolutePath());
                AudioSystem.write(stream, micAudioFileFormatType, file);
            }
            cashFile.delete();
            cashFile = null;
            logger.debug("@SAVE2");
        }
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            logger.error("Ошибка обработчика", e);
        }
        try {
            saveToFile();
        } catch (Exception e) {
            logger.error("Ошибка сохранения файла", e);
        }
        thread = null;
    }

    private void doWork() throws InterruptedException, IOException {
        logger.debug("START");
        while (thread != null) {
            partData = queue.take();
            if (partData.getStartDate() != null) {
                workPartData();
            }
            if (exit && queue.isEmpty()) {
                exit = false;
                break;
            }
        }
    }

    private void workPartData() throws IOException {
        synchronized (lock) {
            boolean activity = checkActivity();
            if (RECORD.equals(recordState)) {
                writeBuffer(partData);
                if (!activity) {
                    recordState = MICRO_PAUSE;
                }
            } else if (MICRO_PAUSE.equals(recordState)) {
                cashPut();
                if (activity) {
                    recordState = RECORD;
                    cashFlush();
                } else {
                    if (cashMemory.getFirstRecEndDate().getTime() + propertyMicrophone.getMicAutoPauseSec() * 1000 < partData.getEndDate().getTime()) {
                        cashFlushAfter(propertyMicrophone.getMicSaveAfterLengthSec());
                        writeBeep();
                        recordState = PAUSE;
                        checkSave();
                    }
                }
            } else {
                cashPut();
                if (activity) {
                    cashMemory.clearUnnecessaryRecords(propertyMicrophone.getMicSaveBeforeLengthSec());
                    cashFlush();
                    recordState = RECORD;
                } else {
                    if (cashFile != null) {
                        if (cashFile.getLastRecEndDate().getTime() + propertyMicrophone.getMicAutoSavePauseSec() * 1000 < partData.getEndDate().getTime()) {
                            saveToFile();
                        }
                    } else {
                        cashMemory.clearUnnecessaryRecords(propertyMicrophone.getMicSaveBeforeLengthSec());
                    }
                }
            }
        }
    }

    private void checkSave() throws IOException {
        final int sizeByte = propertyMicrophone.getMicAutoSaveBySizeMb() * 1024 * 1024;
        final int millis = propertyMicrophone.getMicAutoSaveBySec() * 1000;
        if (cashFile.getChannel().size() >= sizeByte) {
            logger.debug("Сохраняем, так как размер кэша достиг {} Мб", propertyMicrophone.getMicAutoSaveBySizeMb());
            saveToFile();
        } else if (cashFile.getDurationMilliSec() >= millis) {
            logger.debug("Сохраняем, так как длительность записи больше {} сек", propertyMicrophone.getMicAutoSaveBySec());
            saveToFile();
        }
    }

    private void cashFlush() throws IOException {
        for (PartData partData : cashMemory) {
            writeBuffer(partData);
        }
        cashMemory.clear();
    }

    private void cashFlushAfter(double sec) throws IOException {
        if (cashFile == null || cashFile.getLastRecEndDate() == null) {
            logger.debug("Сбрасываем кэш, нечего чистить");
            return;
        }
        logger.debug("Сбрасываем кэш, записей  = {}", cashMemory.size());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cashFile.getLastRecEndDate());
        calendar.add(Calendar.MILLISECOND, (int) (sec * 1000));
        Date date = calendar.getTime();
        Collection<PartData> remotes = new ArrayList<>();

        for (PartData partData : cashMemory) {
            if (date.compareTo(partData.getStartDate()) < 0) {
                cashMemory.removeAll(remotes);
                cashMemory.clearUnnecessaryRecords(propertyMicrophone.getMicSaveBeforeLengthSec());
                break;
            }
            writeBuffer(partData);
            remotes.add(partData);
        }
    }

    private void cashPut() {
        cashMemory.add(partData);
    }

    private void writeBuffer(PartData partData) throws IOException {
        if (cashFile == null) {
            File file;
            final String tmpDir = propertyMicrophone.getTmpDir();
            if (StringUtils.isNoEmpty(tmpDir)) {
                File tmpPath = new File(propertyMicrophone.getTmpDir());
                FileUtils.mkdirs(tmpPath);
                file = File.createTempFile("ms.", ".tmp", tmpPath);
            } else {
                file = File.createTempFile("ms.", ".tmp");
            }
            cashFile = new CashFile(file);
        }
        cashFile.write(partData);
        writingAfterBeep = true;
    }

    private void writeBeep() throws IOException {
        if (beepMicByte == null) {
            logger.warn("BEEP не задан");
        } else if (!writingAfterBeep) {
            logger.debug("BEEP не требуется");
        } else {
            logger.debug("BEEP");
            cashFile.write(beepMicByte);
        }
        writingAfterBeep = false;
    }

    private boolean checkActivity() {
        final int compareValue = PAUSE.equals(recordState) ? propertyMicrophone.getMicSignalValuePauseOff() : propertyMicrophone.getMicSignalValuePauseOn();
        byte[] bytes = partData.getBytes();
        int b = 1;
        byte b1 = 0;
        int average = 0;
        int max = 0;
        boolean result = false;
        for (byte data : bytes) {
            b--;
            if (b == 0) {
                b1 = data;
            } else {
                b = 1;
                int value = abs((b1 & 0xff) | (data << 8));
                average = (average + value) / 2;
                if (value > max) {
                    max = value;
                }
                if (value > compareValue) {
                    result = true;
                    break;
                }
            }
        }
        if ((result && !RECORD.equals(recordState)) || (!result && RECORD.equals(recordState))) {
            logger.debug("{}[comp={}, cur={}, ave={}, max={}]", recordState, compareValue, result, average, max);
        } else {
            logger.trace("{}[comp={}, cur={}, ave={}, max={}]", recordState, compareValue, result, average, max);
        }
        return result;
    }

}
