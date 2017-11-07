package ru.home.sound.microphone;

import org.slf4j.Logger;
import ru.dns.prop4s.ManagerProperty;
import ru.home.common.FileUtils;
import ru.home.common.StringUtils;
import ru.home.common.logger.Slf4Logger;
import ru.home.common.system.UserInfo;
import ru.home.sound.SoundUtils;

import javax.sound.sampled.*;
import java.util.Date;

import static ru.home.common.FileUtils.FileFormat.*;
import static ru.home.common.FileUtils.FileFormat.DURATION_SHORT;
import static ru.home.common.StringUtils.toDate;

public class MicrophoneFactory {

    private static Logger logger = Slf4Logger.getLogger();

    private static AudioInputStream beep;

    public static Microphone newMicrophone() throws LineUnavailableException {
        PropertyMicrophone propertyMicrophone = ManagerProperty.property(PropertyMicrophone.class);
        AudioFormat format = getMicrophoneAudioFormat(propertyMicrophone);
        DataLine.Info info = SoundUtils.getDataLineInfo(format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        Microphone microphone = new Microphone(format, info, line, propertyMicrophone.getMicAudioFileFormatType());
        getMicrophoneBeep(propertyMicrophone, format);
        return microphone;
    }

    private static void getMicrophoneBeep(PropertyMicrophone propertyMicrophone, AudioFormat format) {
        try {
            beep = SoundUtils.getMicBeepAudioInputStream(propertyMicrophone.getMicBeepFile(), format, propertyMicrophone.getMicAudioFileFormatType());
        } catch (Exception e) {
            logger.warn("Ошибка инициализации сигнала", e);
            beep = null;
        }
    }

    public static AudioInputStream getBeep() {
        return beep;
    }

    private static AudioFormat getMicrophoneAudioFormat(PropertyMicrophone propertyMicrophone) {
        logger.debug("");
        AudioFormat audioFormat = new AudioFormat(propertyMicrophone.getMicEncoding(), propertyMicrophone.getMicSampleRate(),
                propertyMicrophone.getMicSampleSizeInBits(), propertyMicrophone.getMicChannels(), propertyMicrophone.getMicFrameSize(),
                propertyMicrophone.getMicFrameRate(), propertyMicrophone.isMicBigEndian());
        logger.debug(audioFormat.toString());
        return audioFormat;
    }

    public static String genericFileName(String regexFileName, Date startRec, double duration, UserInfo userInfo) {
        String result = StringUtils.isNoEmpty(regexFileName) ? regexFileName : PropertyMicrophone.MIC_REC_FILE_NAME;

        for (FileUtils.DateFormat format : FileUtils.DateFormat.values()) {
            result = replaceAll(result, String.valueOf(format), toDate(startRec, format.getFormat()));
        }
        result = replaceAll(result, String.valueOf(IP), userInfo.getIp());
        result = replaceAll(result, String.valueOf(COMPUTER_NAME), userInfo.getComputerName());
        result = replaceAll(result, String.valueOf(USER_NAME), userInfo.getUserName());
        if (duration >= 0) {
            result = replaceAll(result, String.valueOf(DURATION), StringUtils.durationToString(duration));
            result = replaceAll(result, String.valueOf(DURATION_SHORT), StringUtils.durationToShortString(duration));
        }
        return result;
    }

    private static String replaceAll(String string, String regex, String replacement) {
        return StringUtils.replaceAll(string, "\\$" + regex + "\\$", replacement);
    }

    private MicrophoneFactory() {
        super();
    }

}
