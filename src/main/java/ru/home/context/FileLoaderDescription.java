package ru.home.context;

import ru.dns.prop4s.common.helpers.Utils;
import ru.dns.prop4s.loader.FileLoader;

import java.io.File;

import static ru.home.common.FileUtils.*;
import static ru.home.common.logger.PropertyLogger.*;
import static ru.home.context.ProjectConstants.*;

public class FileLoaderDescription extends FileLoader {

    public FileLoaderDescription(File file) {
        super(file);
    }

    @Override
    public StringBuilder getHeader() {
        StringBuilder header = super.getHeader();
        Utils.addComment(header, "logLevel, Logger level", LEVEL.values());
        Utils.addComment(header, "logType, Logger type", TYPE.values());
        Utils.addComment(header, "Audio format encoding", SOUND_TYPES);
        Utils.addComment(header, "Audio file format", SOUND_FILE_TYPES);
        Utils.addComment(header, "Audio rate", SOUND_RATES);
        Utils.addComment(header, "File name date format", DateFormat.values());
        Utils.addComment(header, "File name tags", FileFormat.values());
        return header;
    }

}
