package ru.home.sound.microphone;

import org.slf4j.Logger;
import ru.dns.prop4s.ManagerProperty;
import ru.home.common.logger.Slf4Logger;
import ru.home.sound.stream.helpers.AbstractSound;
import ru.home.sound.SoundUtils;

import javax.sound.sampled.*;

public class Microphone extends AbstractSound {

    private static Logger logger = Slf4Logger.getLogger();

    public Microphone(AudioFormat format, DataLine.Info info, TargetDataLine line, AudioFileFormat.Type audioFileFormatType) throws LineUnavailableException {
        super(format, info, line, audioFileFormatType);
        PropertyMicrophone property = ManagerProperty.property(PropertyMicrophone.class);
        setMicrophoneVolume(property.getMicVolumeNormal(), false);
    }

    /**
     * установить громкость микрофона
     */
    public static void setMicrophoneVolume(int percent, boolean smoothly) {
        PropertyMicrophone property = ManagerProperty.property(PropertyMicrophone.class);
        if (property.isMicEnabled()) {
            try {
                SoundUtils.setMicrophoneVolume(percent, smoothly);
            } catch (Exception e) {
                logger.error("Error set microphone volume", e);
            }
        }
    }

}
