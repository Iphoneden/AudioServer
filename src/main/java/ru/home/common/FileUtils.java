package ru.home.common;

import org.slf4j.Logger;
import ru.home.common.logger.Slf4Logger;

import javax.sound.sampled.AudioFileFormat;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ru.home.common.StringUtils.*;

public class FileUtils {

    private static Logger logger = Slf4Logger.getLogger();

    public enum FileFormat {
        DURATION,
        DURATION_SHORT,
        IP,
        COMPUTER_NAME,
        USER_NAME
    }

    public enum DateFormat {
        DATA_FORMAT_SHORT("MM.dd"),
        DATE_TIME_FORMAT_SHORT("MM.dd HH.mm"),
        TIME_FORMAT_SHORT("HH.mm"),

        DATA_FORMAT("yy.MM.dd"),
        DATE_TIME_FORMAT("yy.MM.dd HH.mm.ss"),
        TIME_FORMAT("HH.mm.ss"),
        TIME_FORMAT_HIGH("mm.ss.SSS"),

        DATA_FORMAT_LONG("yyyy.MM.dd"),
        DATE_TIME_FORMAT_LONG("yyyy.MM.dd HH.mm.ss");

        private String format;

        DateFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    public static String getExtension(String name) {
        if (isEmpty(name)) {
            return null;
        }
        int i = name.lastIndexOf(".");
        return i > 0 ? name.substring(i + 1) : null;
    }

    public static String getNameWithoutExtension(String name) {
        String extension = getExtension(name);
        return isEmpty(extension) ? name : name.substring(0, name.length() - extension.length() - 1);
    }

    public static String normalize(String path) {
        try {
            return new URI(path).normalize().getPath();
        } catch (URISyntaxException e) {
            e.getStackTrace();
        }
        return path;
    }

    public static void delete(File file) {
        boolean delete = file.delete();
        if (!delete) {
            logger.warn("почему-то не удалился {}", file);
        }
    }

    public static void delete(File path, String mask) {
        for (File file : find(path, mask)) {
            delete(file);
        }
    }

    public static File[] find(File path, String mask) {
        final String regex = mask.replace("?", ".?").replace("*", ".*?");
        return path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                boolean matches = name.matches(regex);
                return matches;
            }
        });
    }

    public static void mkdirs(File directory) {
        if (directory != null && (!directory.exists() || !directory.isDirectory())) {
            boolean mkdirs = directory.mkdirs();
            if (!mkdirs) {
                logger.warn("почему-то не удалилась {}", directory);
            }
        }
    }

    public static File getFileName(File path, String name, AudioFileFormat.Type type) {
        String fileName = name + "." + type.getExtension();
        File file;
        int i = 1;
        FileUtils.mkdirs(path);
        do {
            if (path != null) {
                file = new File(path, fileName);
            } else {
                file = new File(fileName);
            }
            fileName = name + "(" + i + ")." + type.getExtension();
            i++;
        } while (file.exists());
        return file;
    }

    public static String getMD5(File file) throws NoSuchAlgorithmException, IOException {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        try (InputStream inputStream = new FileInputStream(file); DigestInputStream stream = new DigestInputStream(inputStream, md5)) {
            byte[] buffer = new byte[8192];
            while (stream.read(buffer) != -1) {
            }
            for (byte b : md5.digest()) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
        }
        return sb.toString();
    }

}

