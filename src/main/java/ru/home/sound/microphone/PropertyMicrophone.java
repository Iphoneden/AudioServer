package ru.home.sound.microphone;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import static ru.home.common.FileUtils.DateFormat.DATA_FORMAT_SHORT;
import static ru.home.common.FileUtils.DateFormat.TIME_FORMAT_SHORT;
import static ru.home.common.FileUtils.FileFormat.DURATION_SHORT;

public class PropertyMicrophone {

    public static final String PATTERN_FILE_NAME = "$"+DATA_FORMAT_SHORT+"$.$"+TIME_FORMAT_SHORT+"$_$"+DURATION_SHORT+"$";
    public static final String MIC_REC_FILE_NAME = "$COMPUTER_NAME$_"+PATTERN_FILE_NAME;
    public static final String BEEP_SONG = "separator.wav";
    public static final int MIC_RATE_INT = 22050;
    public static final String MIC_PATH = "rec";

    private String tmpDir;

    private boolean micEnabled = true;

    private String micRecPath= MIC_PATH;
    private String micRecFileName = MIC_REC_FILE_NAME;

    private AudioFormat.Encoding micEncoding = AudioFormat.Encoding.PCM_SIGNED;
    private float micSampleRate = MIC_RATE_INT;
    private int micSampleSizeInBits = 16;
    private int micChannels = 2;
    private int micFrameSize = 4;
    private float micFrameRate = MIC_RATE_INT;
    private boolean micBigEndian;
    private AudioFileFormat.Type micAudioFileFormatType = AudioFileFormat.Type.WAVE;

    private int micVolumeMin = 10;
    private int micVolumeNormal = 50;
    private int micVolumeMax = 100;

    private int micSignalValuePauseOn = 1000;
    private int micSignalValuePauseOff = 3000;

    private int micAutoPauseSec = 5;
    private int micAutoSavePauseSec = 20;
    private int micAutoSaveBySec = 1000;

    private float micSaveBeforeLengthSec = 0.5f;
    private float micSaveAfterLengthSec = 0.5f;

    private int micAutoSaveBySizeMb = 100;
    private String micBeepFile = BEEP_SONG;

    /**
     * темповая директория , пусто - системная , .. текущя
     * @return string
     */
    public String getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    /**
     * путь для записи с микрофона
     * @return маска файла
     */
    public String getMicRecPath() {
        return micRecPath;
    }

    public void setMicRecPath(String micRecPath) {
        this.micRecPath = micRecPath;
    }

    /**
     * маска файла для записи с микрофона
     * @return маска файла
     */
    public String getMicRecFileName() {
        return micRecFileName;
    }

    public void setMicRecFileName(String micRecFileName) {
        this.micRecFileName = micRecFileName;
    }

    public AudioFormat.Encoding getMicEncoding() {
        return micEncoding;
    }

    public void setMicEncoding(AudioFormat.Encoding micEncoding) {
        this.micEncoding = micEncoding;
    }

    public float getMicSampleRate() {
        return micSampleRate;
    }

    public void setMicSampleRate(float micSampleRate) {
        this.micSampleRate = micSampleRate;
    }

    public int getMicSampleSizeInBits() {
        return micSampleSizeInBits;
    }

    public void setMicSampleSizeInBits(int micSampleSizeInBits) {
        this.micSampleSizeInBits = micSampleSizeInBits;
    }

    public int getMicChannels() {
        return micChannels;
    }

    public void setMicChannels(int micChannels) {
        this.micChannels = micChannels;
    }

    public int getMicFrameSize() {
        return micFrameSize;
    }

    public void setMicFrameSize(int micFrameSize) {
        this.micFrameSize = micFrameSize;
    }

    public float getMicFrameRate() {
        return micFrameRate;
    }

    public void setMicFrameRate(float micFrameRate) {
        this.micFrameRate = micFrameRate;
    }

    public boolean isMicBigEndian() {
        return micBigEndian;
    }

    public void setMicBigEndian(boolean micBigEndian) {
        this.micBigEndian = micBigEndian;
    }

    public AudioFileFormat.Type getMicAudioFileFormatType() {
        return micAudioFileFormatType;
    }

    public void setMicAudioFileFormatType(AudioFileFormat.Type micAudioFileFormatType) {
        this.micAudioFileFormatType = micAudioFileFormatType;
    }

    public boolean isMicEnabled() {
        return micEnabled;
    }

    public void setMicEnabled(boolean micEnabled) {
        this.micEnabled = micEnabled;
    }

    /**
     * минимальнвя громкасть микрофона в %
     */
    public int getMicVolumeMin() {
        return micVolumeMin;
    }

    public void setMicVolumeMin(int micVolumeMin) {
        this.micVolumeMin = micVolumeMin;
    }

    /**
     * максимальная громкасть микрофона в %
     */
    public int getMicVolumeMax() {
        return micVolumeMax;
    }

    public void setMicVolumeMax(int micVolumeMax) {
        this.micVolumeMax = micVolumeMax;
    }

    /**
     * стартовая громкасть микрофона в %
     */
    public int getMicVolumeNormal() {
        return micVolumeNormal;
    }

    public void setMicVolumeNormal(int micVolumeNormal) {
        this.micVolumeNormal = micVolumeNormal;
    }

    /**
     * Значение громкости звука при котором активность считается отрицательной(если активность включена)
     */
    public int getMicSignalValuePauseOn() {
        return micSignalValuePauseOn;
    }

    public void setMicSignalValuePauseOn(int micSignalValuePauseOn) {
        this.micSignalValuePauseOn = micSignalValuePauseOn;
    }

    /**
     * Значение громкости звука при котором считается активность положительной
     */
    public int getMicSignalValuePauseOff() {
        return micSignalValuePauseOff;
    }

    public void setMicSignalValuePauseOff(int micSignalValuePauseOff) {
        this.micSignalValuePauseOff = micSignalValuePauseOff;
    }

    /**
     * если нет активности, включает паузу записи микрофона через указанное кол. секун
     * @return секунды
     */
    public int getMicAutoPauseSec() {
        return micAutoPauseSec;
    }

    public void setMicAutoPauseSec(int micAutoPauseSec) {
        this.micAutoPauseSec = micAutoPauseSec;
    }

    /**
     * если нет активности, сохраняет в файл запись поставленную на пауза через указанное кол. секун
     * @return секунды
     */
    public int getMicAutoSavePauseSec() {
        return micAutoSavePauseSec;
    }

    public void setMicAutoSavePauseSec(int micAutoSavePauseSec) {
        this.micAutoSavePauseSec = micAutoSavePauseSec;
    }

    /**
     * сохраняет запись в файл если она длится больше указанное кол. секунд
     * @return секунды
     */
    public int getMicAutoSaveBySec() {
        return micAutoSaveBySec;
    }

    public void setMicAutoSaveBySec(int micAutoSaveBySec) {
        this.micAutoSaveBySec = micAutoSaveBySec;
    }

    /**
     * количество секунд неактивного участка в переди записи
     * @return секунды
     */
    public float getMicSaveBeforeLengthSec() {
        return micSaveBeforeLengthSec;
    }

    public void setMicSaveBeforeLengthSec(float micSaveBeforeLengthSec) {
        this.micSaveBeforeLengthSec = micSaveBeforeLengthSec;
    }

    /**
     * количество секунд неактивного участка в конце записи
     * @return секунды
     */
    public float getMicSaveAfterLengthSec() {
        return micSaveAfterLengthSec;
    }

    public void setMicSaveAfterLengthSec(float micSaveAfterLengthSec) {
        this.micSaveAfterLengthSec = micSaveAfterLengthSec;
    }

    /**
     * сохраняет запись в файл если предельный размер файла
     */
    public int getMicAutoSaveBySizeMb() {
        return micAutoSaveBySizeMb;
    }

    public void setMicAutoSaveBySizeMb(int micAutoSaveBySizeMb) {
        this.micAutoSaveBySizeMb = micAutoSaveBySizeMb;
    }

    /**
     * путь к файлу со коротким гудка для микрофона
     * @return имя файла
     */
    public String getMicBeepFile() {
        return micBeepFile;
    }

    public void setMicBeepFile(String micBeepFile) {
        this.micBeepFile = micBeepFile;
    }

    @Override
    public String toString() {
        return "PropertyMicrophone{" +
                "tmpDir='" + tmpDir + '\'' +
                ", micEnabled=" + micEnabled +
                ", micRecPath='" + micRecPath + '\'' +
                ", micRecFileName='" + micRecFileName + '\'' +
                ", micEncoding=" + micEncoding +
                ", micSampleRate=" + micSampleRate +
                ", micSampleSizeInBits=" + micSampleSizeInBits +
                ", micChannels=" + micChannels +
                ", micFrameSize=" + micFrameSize +
                ", micFrameRate=" + micFrameRate +
                ", micBigEndian=" + micBigEndian +
                ", micAudioFileFormatType=" + micAudioFileFormatType +
                ", micVolumeMin=" + micVolumeMin +
                ", micVolumeNormal=" + micVolumeNormal +
                ", micVolumeMax=" + micVolumeMax +
                ", micSignalValuePauseOn=" + micSignalValuePauseOn +
                ", micSignalValuePauseOff=" + micSignalValuePauseOff +
                ", micAutoPauseSec=" + micAutoPauseSec +
                ", micAutoSavePauseSec=" + micAutoSavePauseSec +
                ", micAutoSaveBySec=" + micAutoSaveBySec +
                ", micSaveBeforeLengthSec=" + micSaveBeforeLengthSec +
                ", micSaveAfterLengthSec=" + micSaveAfterLengthSec +
                ", micAutoSaveBySizeMb=" + micAutoSaveBySizeMb +
                ", micBeepFile='" + micBeepFile + '\'' +
                '}';
    }

}
