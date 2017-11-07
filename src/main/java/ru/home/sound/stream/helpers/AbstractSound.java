package ru.home.sound.stream.helpers;

import org.slf4j.Logger;
import ru.home.common.logger.Slf4Logger;

import javax.sound.sampled.*;

public abstract class AbstractSound implements Sound {

    private static Logger logger = Slf4Logger.getLogger();

    private AudioFormat format;
    private DataLine.Info info;
    private TargetDataLine line;
    private AudioFileFormat.Type audioFileFormatType;

    public AbstractSound(AudioFormat format, DataLine.Info info, TargetDataLine line, AudioFileFormat.Type audioFileFormatType) throws LineUnavailableException {
        super();
        this.format = format;
        this.info = info;
        this.line = line;
        this.audioFileFormatType = audioFileFormatType;
        final int bufferSize = line.getBufferSize();
        logger.debug("line.open (bufferSize = {})", bufferSize);
        line.open(format, bufferSize);
        logger.debug("line.open ok");
        line.start();
        logger.debug("line.start ok");
    }

    @Override
    public AudioFormat getFormat() {
        return format;
    }

    @Override
    public DataLine.Info getInfo() {
        return info;
    }

    @Override
    public TargetDataLine getLine() {
        return line;
    }

    @Override
    public AudioFileFormat.Type getAudioFileFormatType() {
        return audioFileFormatType;
    }

}
