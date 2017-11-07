package ru.home.sound.stream.helpers;

import ru.home.common.FileUtils;

import java.io.*;
import java.util.Date;

public class CashFile extends FileOutputStream {

    private final File file;
    private PartData firstRec;
    private PartData lastRec;

    public CashFile(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        super.close();
        firstRec = null;
        lastRec = null;
    }

    public void delete() throws IOException {
        close();
        FileUtils.delete(file);
    }

    public void write(PartData partData) throws IOException {
        byte[] bytes = partData.getBytes();
        super.write(bytes);
        if (firstRec == null) {
            firstRec = partData;
        }
        lastRec = partData;
    }

    public Date getLastRecEndDate() {
        return lastRec != null ? lastRec.getEndDate() : null;
    }

    public Date getFirstRecStartDate() {
        return firstRec != null ? firstRec.getStartDate() : null;
    }

    public InputStream getInputStream() throws IOException {
        flush();
        return new FileInputStream(file);
    }

    public long getDurationMilliSec() {
        Date start = getFirstRecStartDate();
        if (start == null) {
            return 0;
        }
        Date end = getLastRecEndDate();
        if (end != null) {
            return end.getTime() - start.getTime();
        }
        return firstRec.getEndDate() != null ? firstRec.getEndDate().getTime()-start.getTime() : 0;
    }
}
