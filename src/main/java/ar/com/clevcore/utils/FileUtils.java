package ar.com.clevcore.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public static String fileToString(String path) throws IOException, URISyntaxException {
        return fileToString(path, "utf-8");
    }

    public static String fileToString(String path, String encoding) throws IOException, URISyntaxException {
        URI uri = FileUtils.class.getClassLoader().getResource(path).toURI();

        byte[] encoded = Files.readAllBytes(Paths.get(uri));

        return new String(encoded, encoding);
    }

}
