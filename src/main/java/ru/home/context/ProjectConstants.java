package ru.home.context;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public class ProjectConstants {

    public static final int SLEEP_MILLIS_INT = 10000;

    private ProjectConstants() {
        super();
    }

    public static final AudioFormat.Encoding[] SOUND_TYPES = {
            AudioFormat.Encoding.PCM_SIGNED,
            AudioFormat.Encoding.PCM_UNSIGNED,
            AudioFormat.Encoding.PCM_FLOAT,
            AudioFormat.Encoding.ULAW,
            AudioFormat.Encoding.ALAW
    };

    public static final AudioFileFormat.Type[] SOUND_FILE_TYPES = {
            AudioFileFormat.Type.WAVE,
            AudioFileFormat.Type.AU,
            AudioFileFormat.Type.AIFF,
            AudioFileFormat.Type.AIFC,
            AudioFileFormat.Type.SND
    };

    public static final float[] SOUND_RATES = {
            8000, 11025, 16000, 22050, 32000, 44100, 48000
    };

 }
