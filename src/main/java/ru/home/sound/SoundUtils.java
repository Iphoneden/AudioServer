package ru.home.sound;

import org.slf4j.Logger;
import ru.home.common.StringUtils;
import ru.home.common.logger.Slf4Logger;

import javax.sound.sampled.*;
import java.io.*;

import static ru.home.sound.microphone.PropertyMicrophone.BEEP_SONG;

public class SoundUtils {

    private static Logger logger = Slf4Logger.getLogger();

    private static final int BUFFER_SIZE = 1024;

    private static AudioInputStream loadFromFile(File file, AudioFormat format, AudioFileFormat.Type type) throws IOException, UnsupportedAudioFileException {
        if (AudioSystem.getAudioFileFormat(file) == null) {
            throw new UnsupportedAudioFileException(file.toString());
        }
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        logger.debug(file + " frame length = " + stream.getFrameLength() + " " + stream.getFormat());
        if (type != null) {
            stream = converter(stream, format, type);
        }
        return stream;
    }

    private static AudioInputStream converter(AudioInputStream stream, AudioFormat format, AudioFileFormat.Type type) throws IOException, UnsupportedAudioFileException {
        if (!AudioSystem.isFileTypeSupported(type, stream)) {
            throw new UnsupportedAudioFileException("Не поддерживается конвертация в " + type.getExtension());
        }
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(format, stream)) {
            logger.debug("\n{} -> \n{}", stream.getFormat(), inputStream.getFormat());
            return duplicate(inputStream, stream.getFrameLength());
        }
    }

    private static AudioInputStream duplicate(AudioInputStream stream, long frameLength) throws IOException {
        int length = 0;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[BUFFER_SIZE];
            int byteRead;
            while ((byteRead = stream.read(bytes)) > 0) {
                out.write(bytes, 0, byteRead);
                length += byteRead;
            }
            AudioInputStream inputStream = new AudioInputStream(new ByteArrayInputStream(out.toByteArray()), stream.getFormat(), frameLength * 2);
            logger.debug("{}, byte length = {}, frame length = {}", inputStream.getFormat(), length, inputStream.getFrameLength());
            return inputStream;
        }
    }

    public static byte[] getByte(AudioInputStream stream) throws IOException {
        if (stream == null) {
            return null;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[BUFFER_SIZE];
            int byteRead;
            while ((byteRead = stream.read(bytes)) > 0) {
                out.write(bytes, 0, byteRead);
            }
            return out.toByteArray();
        }
    }

    public static DataLine.Info getDataLineInfo(AudioFormat format) throws LineUnavailableException {
        logger.debug("geting DataLine.Info info...{}", format);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Line matching " + info + " not supported.");
        }
        logger.info("DataLine.Info info: {}",info.toString());
        return info;
    }

    public static AudioInputStream getMicBeepAudioInputStream(String fileName, AudioFormat format, AudioFileFormat.Type type) throws IOException, UnsupportedAudioFileException {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(fileName);
        try {
            return loadFromFile(file, format, type);
        } catch (Exception e) {
            logger.warn("Ошибка инициализации сигнала {}", file.getAbsolutePath(), e);
            if (BEEP_SONG.equals(fileName)) {
                InputStream resourceAsStream = SoundUtils.class.getClassLoader().getResourceAsStream(BEEP_SONG);
                try {
                    return AudioSystem.getAudioInputStream(resourceAsStream);
                } catch (Exception e1) {
                    logger.warn("Ошибка инициализации сигнала по умолчанию", e1);
                }
            }
        }
        return null;
    }

    /**
     * Громкость микрофона
     */
    public static void setMicrophoneVolume(int percent, boolean smoothly) throws LineUnavailableException {
        if (percent > 0 && percent <= 100) {
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            for (Mixer.Info mixerInfo : mixerInfos) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                if (mixer.getMaxLines(Port.Info.MICROPHONE) > 0) {
                    Port lineIn = (Port) mixer.getLine(Port.Info.MICROPHONE);
                    lineIn.open();
                    CompoundControl cc = (CompoundControl) lineIn.getControls()[0];
                    Control[] controls = cc.getMemberControls();
                    for (Control c : controls) {
                        if (c instanceof FloatControl) {
                            FloatControl floatControl = (FloatControl) c;
                            int value = (int) (floatControl.getValue() * 100);
                            if (percent != value) {
                                if (smoothly) {
                                    int newValue = value;
                                    int count = 50;
                                    do {
                                        newValue = newValue + (value > percent ? -1 : 1);
                                        floatControl.setValue((float) newValue / 100);
                                    } while (count-- > 0 && value == (int) (floatControl.getValue() * 100));
                                    logger.trace("setMicrophoneVolume[{}] {} <= {} seek {}", count, value, newValue, percent);
                                } else {
                                    floatControl.setValue((float) percent / 100);
                                    logger.trace("setMicrophoneVolume {} <= {} ", value, percent);
                                }
                            }
                        }
                    }
                    lineIn.close();
                }
            }
        }
    }

}
