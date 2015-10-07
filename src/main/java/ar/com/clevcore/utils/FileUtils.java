package ar.com.clevcore.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class FileUtils {

    private FileUtils() {
        throw new AssertionError();
    }

    public static boolean makeDir(String path) {
        return new File(path).mkdirs();
    }

    public static void saveFile(String path, String name, byte[] file) throws IOException {
        makeDir(path);

        OutputStream outputStream = new FileOutputStream(new File(path + File.separator + name));
        outputStream.write(file);
        outputStream.close();
    }

    public static boolean deleteFile(String path, String name) {
        return new File(path + File.separator + name).delete();
    }

}
