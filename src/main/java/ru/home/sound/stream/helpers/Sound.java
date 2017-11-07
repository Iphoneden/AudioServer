package ru.home.sound.stream.helpers;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public interface Sound {

    AudioFormat getFormat();

    DataLine.Info getInfo();

    TargetDataLine getLine();

    AudioFileFormat.Type getAudioFileFormatType();
}
